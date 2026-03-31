package com.semantic.semantic_test.repo;

import com.semantic.semantic_test.domain.Quote;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryQuoteRepository implements QuoteRepository {
	private final ConcurrentHashMap<String, Quote> store = new ConcurrentHashMap<>();

	@Override
	public void save(Quote quote) {
		store.put(quote.quoteId(), quote);
	}

	@Override
	public Optional<Quote> findById(String quoteId) {
		return Optional.ofNullable(store.get(quoteId));
	}
}

