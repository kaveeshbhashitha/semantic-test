package com.semantic.semantic_test.api.dto;

import java.math.BigDecimal;

public record QuoteItemRequest(
		String sku,
		ItemCategory category,
		BigDecimal unitPrice,
		int quantity
) {
}

