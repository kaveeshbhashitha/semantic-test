package com.semantic.semantic_test.error;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(ValidationException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(
				Instant.now(),
				"BAD_REQUEST",
				ex.getMessage(),
				ex.getErrors()
		));
	}

	@ExceptionHandler(QuoteNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(QuoteNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(
				Instant.now(),
				"NOT_FOUND",
				ex.getMessage(),
				java.util.List.of("quoteId=" + ex.getQuoteId())
		));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
		log.error("unhandled_exception", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse(
				Instant.now(),
				"INTERNAL_SERVER_ERROR",
				"Unexpected error",
				java.util.List.of()
		));
	}
}

