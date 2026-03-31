package com.semantic.semantic_test.error;

public class IdempotencyConflictException extends RuntimeException {
	private final String idempotencyKey;
	private final String existingCustomerId;
	private final String requestedCustomerId;

	public IdempotencyConflictException(String idempotencyKey, String existingCustomerId, String requestedCustomerId) {
		super("Idempotency-Key conflict");
		this.idempotencyKey = idempotencyKey;
		this.existingCustomerId = existingCustomerId;
		this.requestedCustomerId = requestedCustomerId;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public String getExistingCustomerId() {
		return existingCustomerId;
	}

	public String getRequestedCustomerId() {
		return requestedCustomerId;
	}
}

