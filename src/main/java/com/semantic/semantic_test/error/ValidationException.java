package com.semantic.semantic_test.error;

import java.util.List;

public class ValidationException extends RuntimeException {
	private final List<String> errors;

	public ValidationException(List<String> errors) {
		super("Validation failed");
		this.errors = List.copyOf(errors);
	}

	public List<String> getErrors() {
		return errors;
	}
}

