package com.semantic.semantic_test.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record QuoteResponse(
		String quoteId,
		String customerId,
		CustomerTier customerTier,
		Instant createdAt,
		BigDecimal subtotal,
		BigDecimal discountTotal,
		BigDecimal tax,
		BigDecimal shipping,
		BigDecimal total,
		int riskScore,
		boolean approved,
		List<String> messages
) {
}

