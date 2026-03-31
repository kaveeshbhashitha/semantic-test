package com.semantic.semantic_test.domain;

import java.util.List;

public record QuoteRisk(
		int riskScore,
		boolean approved,
		List<String> messages
) {
}

