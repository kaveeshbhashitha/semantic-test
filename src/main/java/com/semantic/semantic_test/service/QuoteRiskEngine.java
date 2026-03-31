package com.semantic.semantic_test.service;

import com.semantic.semantic_test.api.dto.CustomerTier;
import com.semantic.semantic_test.api.dto.ItemCategory;
import com.semantic.semantic_test.domain.QuoteItem;
import com.semantic.semantic_test.domain.QuoteRisk;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class QuoteRiskEngine {
	public QuoteRisk score(CustomerTier tier, BigDecimal total, List<QuoteItem> items) {
		int score = 0;
		List<String> messages = new ArrayList<>();

		if (total.compareTo(new BigDecimal("500.00")) >= 0) {
			score += 25;
			messages.add("High total amount");
		}

		int luxuryQty = 0;
		for (QuoteItem item : items) {
			if (item.category() == ItemCategory.LUXURY) {
				luxuryQty += item.quantity();
			}
		}
		if (luxuryQty >= 3) {
			score += 30;
			messages.add("Bulk luxury purchase");
		} else if (luxuryQty > 0) {
			score += 10;
			messages.add("Contains luxury items");
		}

		if (tier == CustomerTier.BRONZE) {
			score += 10;
			messages.add("New/low-tier customer");
		} else if (tier == CustomerTier.GOLD) {
			score -= 5;
			messages.add("Trusted customer tier");
		}

		boolean approved = score < 40;
		if (!approved) {
			messages.add("Manual review required");
		}

		return new QuoteRisk(score, approved, messages);
	}
}

