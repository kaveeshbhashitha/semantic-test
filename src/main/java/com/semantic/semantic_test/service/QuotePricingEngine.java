package com.semantic.semantic_test.service;

import com.semantic.semantic_test.api.dto.CustomerTier;
import com.semantic.semantic_test.api.dto.ItemCategory;
import com.semantic.semantic_test.domain.QuoteItem;
import com.semantic.semantic_test.domain.QuotePricing;
import com.semantic.semantic_test.error.InvalidCouponException;
import com.semantic.semantic_test.util.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QuotePricingEngine {
	private static final Logger log = LoggerFactory.getLogger(QuotePricingEngine.class);

	public QuotePricing price(
			CustomerTier tier,
			String shippingPostalCode,
			String couponCode,
			List<QuoteItem> items
	) {
		BigDecimal subtotal = Money.zero();
		BigDecimal luxurySubtotal = Money.zero();
		BigDecimal foodSubtotal = Money.zero();
		BigDecimal medicalSubtotal = Money.zero();

		for (QuoteItem item : items) {
			BigDecimal normalizedUnitPrice = Money.round2(item.unitPrice());
			BigDecimal line = Money.mul(normalizedUnitPrice, item.quantity());
			subtotal = Money.add(subtotal, line);
			if (item.category() == ItemCategory.LUXURY) {
				luxurySubtotal = Money.add(luxurySubtotal, line);
			} else if (item.category() == ItemCategory.FOOD) {
				foodSubtotal = Money.add(foodSubtotal, line);
			} else if (item.category() == ItemCategory.MEDICAL) {
				medicalSubtotal = Money.add(medicalSubtotal, line);
			}
		}

		BigDecimal tierDiscountRate = switch (tier) {
			case BRONZE -> new BigDecimal("0.00");
			case SILVER -> new BigDecimal("0.03");
			case GOLD -> new BigDecimal("0.07");
		};
		BigDecimal tierDiscount = Money.round2(subtotal.multiply(tierDiscountRate));

		BigDecimal couponDiscount = Money.zero();
		if (couponCode != null && !couponCode.trim().isEmpty()) {
			String normalized = couponCode.trim().toUpperCase();
			if (normalized.equals("SAVE10")) {
				if (subtotal.compareTo(new BigDecimal("100.00")) >= 0) {
					couponDiscount = new BigDecimal("10.00");
				} else {
					log.info("coupon_rejected couponCode={} reason=subtotal_too_low subtotal={}", normalized, subtotal);
					throw new InvalidCouponException(normalized, "Subtotal must be >= 100.00 for SAVE10");
				}
			} else if (normalized.equals("BLACKFRIDAY")) {
				if (foodSubtotal.compareTo(Money.zero()) > 0 || medicalSubtotal.compareTo(Money.zero()) > 0) {
					log.info("coupon_rejected couponCode={} reason=contains_exempt_categories", normalized);
					throw new InvalidCouponException(normalized, "BLACKFRIDAY cannot be applied to FOOD or MEDICAL items");
				}

				BigDecimal eligible = Money.max(subtotal, Money.zero());
				BigDecimal percent = eligible.multiply(new BigDecimal("0.15"));
				BigDecimal capped = Money.min(Money.round2(percent), new BigDecimal("50.00"));
				couponDiscount = capped;
			} else if (normalized.equals("FREESHIP")) {
				couponDiscount = Money.zero();
			} else {
				log.info("coupon_rejected couponCode={} reason=unknown", normalized);
				throw new InvalidCouponException(normalized, "Unknown coupon code");
			}
		}

		BigDecimal discountTotal = Money.min(Money.add(tierDiscount, couponDiscount), subtotal);
		BigDecimal discountedSubtotal = Money.sub(subtotal, discountTotal);

		BigDecimal tax = computeTax(shippingPostalCode, discountedSubtotal, foodSubtotal, medicalSubtotal);
		BigDecimal shipping = computeShipping(shippingPostalCode, discountedSubtotal, couponCode);
		BigDecimal total = Money.add(Money.add(discountedSubtotal, tax), shipping);

		return new QuotePricing(
				Money.round2(subtotal),
				Money.round2(discountTotal),
				Money.round2(tax),
				Money.round2(shipping),
				Money.round2(total)
		);
	}

	private static BigDecimal computeShipping(String shippingPostalCode, BigDecimal discountedSubtotal, String couponCode) {
		boolean freeShipCoupon = couponCode != null && couponCode.trim().equalsIgnoreCase("FREESHIP");
		if (freeShipCoupon) {
			return Money.zero();
		}
		if (discountedSubtotal.compareTo(new BigDecimal("50.00")) >= 0) {
			return Money.zero();
		}
		boolean remote = shippingPostalCode != null && shippingPostalCode.trim().startsWith("9");
		return remote ? new BigDecimal("8.99") : new BigDecimal("4.99");
	}

	private static BigDecimal computeTax(
			String shippingPostalCode,
			BigDecimal discountedSubtotal,
			BigDecimal foodSubtotal,
			BigDecimal medicalSubtotal
	) {
		BigDecimal baseRate = (shippingPostalCode != null && shippingPostalCode.trim().startsWith("0"))
				? new BigDecimal("0.09")
				: new BigDecimal("0.07");

		BigDecimal exempt = Money.add(foodSubtotal, medicalSubtotal);
		BigDecimal taxable = Money.max(Money.sub(discountedSubtotal, exempt), Money.zero());
		return taxable.multiply(baseRate).setScale(2, RoundingMode.HALF_UP);
	}
}

