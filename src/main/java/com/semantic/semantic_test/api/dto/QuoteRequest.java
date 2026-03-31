package com.semantic.semantic_test.api.dto;

import java.util.List;

public record QuoteRequest(
		String customerId,
		CustomerTier customerTier,
		String shippingPostalCode,
		String couponCode,
		List<QuoteItemRequest> items
) {
}

