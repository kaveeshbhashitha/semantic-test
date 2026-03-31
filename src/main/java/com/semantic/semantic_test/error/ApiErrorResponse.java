package com.semantic.semantic_test.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
		Instant timestamp,
		String error,
		String message,
		List<String> details
) {
}

