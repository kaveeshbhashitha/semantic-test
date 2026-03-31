package com.semantic.semantic_test.domain;

import com.semantic.semantic_test.api.dto.ItemCategory;
import java.math.BigDecimal;

public record QuoteItem(
		String sku,
		ItemCategory category,
		BigDecimal unitPrice,
		int quantity
) {
}

