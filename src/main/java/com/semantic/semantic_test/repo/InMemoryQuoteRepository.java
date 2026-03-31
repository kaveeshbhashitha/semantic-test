package com.semantic.semantic_test.repo;

import com.semantic.semantic_test.domain.Quote;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

	@Override
	public List<Quote> findAll() {
		ArrayList<Quote> quotes = new ArrayList<>(store.values());
		quotes.sort(Comparator.comparing(Quote::createdAt).reversed());
		return quotes;
	}
}

