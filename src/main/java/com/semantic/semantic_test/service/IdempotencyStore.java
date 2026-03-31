package com.semantic.semantic_test.service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class IdempotencyStore {
	private final ConcurrentHashMap<String, String> keyToQuoteId = new ConcurrentHashMap<>();

	public Optional<String> getQuoteId(String key) {
		return Optional.ofNullable(keyToQuoteId.get(key));
	}

	public void putIfAbsent(String key, String quoteId) {
		keyToQuoteId.putIfAbsent(key, quoteId);
	}
}

