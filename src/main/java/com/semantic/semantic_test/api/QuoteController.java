package com.semantic.semantic_test.api;

import com.semantic.semantic_test.api.dto.QuoteRequest;
import com.semantic.semantic_test.api.dto.QuoteResponse;
import com.semantic.semantic_test.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {
	private final QuoteService quoteService;

	public QuoteController(QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public QuoteResponse create(@RequestBody QuoteRequest request) {
		return quoteService.createQuote(request);
	}

	@GetMapping("/{quoteId}")
	public QuoteResponse get(@PathVariable String quoteId) {
		return quoteService.getQuote(quoteId);
	}
}

