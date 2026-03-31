package com.semantic.semantic_test.repo;

import com.semantic.semantic_test.domain.Quote;
import java.util.List;
import java.util.Optional;

public interface QuoteRepository {
	void save(Quote quote);

	Optional<Quote> findById(String quoteId);

	List<Quote> findAll();
}

