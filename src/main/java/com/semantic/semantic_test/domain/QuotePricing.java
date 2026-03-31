package com.semantic.semantic_test.domain;

import java.math.BigDecimal;

public record QuotePricing(
		BigDecimal subtotal,
		BigDecimal discountTotal,
		BigDecimal tax,
		BigDecimal shipping,
		BigDecimal total
) {
}

