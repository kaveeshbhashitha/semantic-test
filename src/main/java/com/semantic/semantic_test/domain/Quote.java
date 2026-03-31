package com.semantic.semantic_test.domain;

import com.semantic.semantic_test.api.dto.CustomerTier;
import java.time.Instant;
import java.util.List;

public record Quote(
		String quoteId,
		String customerId,
		CustomerTier customerTier,
		Instant createdAt,
		List<QuoteItem> items,
		QuotePricing pricing,
		QuoteRisk risk
) {
}

