package com.semantic.semantic_test.service;

import com.semantic.semantic_test.api.dto.QuoteItemRequest;
import com.semantic.semantic_test.api.dto.QuoteRequest;
import com.semantic.semantic_test.error.ValidationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class QuoteValidator {
	private static final int MAX_ITEMS_PER_QUOTE = 20;
	private static final int MAX_QUANTITY_PER_ITEM = 50;

	public void validateCreateRequest(QuoteRequest request) {
		List<String> errors = new ArrayList<>();
		if (request == null) {
			errors.add("request must not be null");
			throw new ValidationException(errors);
		}

		if (isBlank(request.customerId())) {
			errors.add("customerId is required");
		}
		if (request.customerTier() == null) {
			errors.add("customerTier is required");
		}
		if (isBlank(request.shippingPostalCode())) {
			errors.add("shippingPostalCode is required");
		}
		if (request.items() == null || request.items().isEmpty()) {
			errors.add("items must not be empty");
		} else {
			if (request.items().size() > MAX_ITEMS_PER_QUOTE) {
				errors.add("items must not exceed " + MAX_ITEMS_PER_QUOTE + " entries");
			}
			for (int i = 0; i < request.items().size(); i++) {
				QuoteItemRequest item = request.items().get(i);
				if (item == null) {
					errors.add("items[" + i + "] must not be null");
					continue;
				}
				if (isBlank(item.sku())) {
					errors.add("items[" + i + "].sku is required");
				}
				if (item.category() == null) {
					errors.add("items[" + i + "].category is required");
				}
				if (item.unitPrice() == null) {
					errors.add("items[" + i + "].unitPrice is required");
				} else if (item.unitPrice().compareTo(BigDecimal.ZERO) < 0) {
					errors.add("items[" + i + "].unitPrice must be >= 0");
				}
				if (item.quantity() <= 0) {
					errors.add("items[" + i + "].quantity must be >= 1");
				} else if (item.quantity() > MAX_QUANTITY_PER_ITEM) {
					errors.add("items[" + i + "].quantity must not exceed " + MAX_QUANTITY_PER_ITEM);
				}
			}
		}

		if (!errors.isEmpty()) {
			throw new ValidationException(errors);
		}
	}

	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}
}

