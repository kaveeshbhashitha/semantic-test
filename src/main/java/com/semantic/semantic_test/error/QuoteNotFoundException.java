package com.semantic.semantic_test.error;

public class QuoteNotFoundException extends RuntimeException {
	private final String quoteId;

	public QuoteNotFoundException(String quoteId) {
		super("Quote not found: " + quoteId);
		this.quoteId = quoteId;
	}

	public String getQuoteId() {
		return quoteId;
	}
}

