package com.semantic.semantic_test.service;

import com.semantic.semantic_test.api.dto.CustomerTier;
import com.semantic.semantic_test.api.dto.ItemCategory;
import com.semantic.semantic_test.api.dto.QuoteItemRequest;
import com.semantic.semantic_test.api.dto.QuoteRequest;
import com.semantic.semantic_test.api.dto.QuoteResponse;
import com.semantic.semantic_test.domain.Quote;
import com.semantic.semantic_test.domain.QuoteItem;
import com.semantic.semantic_test.domain.QuotePricing;
import com.semantic.semantic_test.domain.QuoteRisk;
import com.semantic.semantic_test.error.QuoteNotFoundException;
import com.semantic.semantic_test.repo.QuoteRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {
	private static final Logger log = LoggerFactory.getLogger(QuoteService.class);

	private final QuoteRepository quoteRepository;
	private final QuoteValidator quoteValidator;
	private final QuotePricingEngine quotePricingEngine;
	private final QuoteRiskEngine quoteRiskEngine;
	private final Clock clock;

	public QuoteService(
			QuoteRepository quoteRepository,
			QuoteValidator quoteValidator,
			QuotePricingEngine quotePricingEngine,
			QuoteRiskEngine quoteRiskEngine,
			Clock clock
	) {
		this.quoteRepository = quoteRepository;
		this.quoteValidator = quoteValidator;
		this.quotePricingEngine = quotePricingEngine;
		this.quoteRiskEngine = quoteRiskEngine;
		this.clock = clock;
	}

	public QuoteResponse createQuote(QuoteRequest request) {
		quoteValidator.validateCreateRequest(request);

		String quoteId = UUID.randomUUID().toString();
		Instant now = clock.instant();

		List<QuoteItem> items = new ArrayList<>();
		for (QuoteItemRequest item : request.items()) {
			// Normalize SKU for consistent downstream comparisons/audit logs
			String normalizedSku = item.sku().trim().toUpperCase();
			items.add(new QuoteItem(normalizedSku, item.category(), item.unitPrice(), item.quantity()));
		}

		QuotePricing pricing = quotePricingEngine.price(
				request.customerTier(),
				request.shippingPostalCode(),
				request.couponCode(),
				items
		);

		QuoteRisk risk = quoteRiskEngine.score(request.customerTier(), pricing.total(), items);

		Quote quote = new Quote(
				quoteId,
				request.customerId(),
				request.customerTier(),
				now,
				items,
				pricing,
				risk
		);

		quoteRepository.save(quote);

		log.info("quote_created quoteId={} customerId={} total={} riskScore={} approved={}",
				quoteId, request.customerId(), pricing.total(), risk.riskScore(), risk.approved());

		return toResponse(quote);
	}

	public QuoteResponse getQuote(String quoteId) {
		Objects.requireNonNull(quoteId, "quoteId");
		Quote quote = quoteRepository.findById(quoteId).orElseThrow(() -> new QuoteNotFoundException(quoteId));
		return toResponse(quote);
	}

	public List<QuoteResponse> listQuotes() {
		return quoteRepository.findAll().stream().map(QuoteService::toResponse).toList();
	}

	private static QuoteResponse toResponse(Quote quote) {
		QuotePricing p = quote.pricing();
		QuoteRisk r = quote.risk();
		return new QuoteResponse(
				quote.quoteId(),
				quote.customerId(),
				quote.customerTier(),
				quote.createdAt(),
				p.subtotal(),
				p.discountTotal(),
				p.tax(),
				p.shipping(),
				p.total(),
				r.riskScore(),
				r.approved(),
				r.messages()
		);
	}
}

