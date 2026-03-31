package com.semantic.semantic_test.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {
	private Money() {
	}

	public static BigDecimal zero() {
		return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
	}

	public static BigDecimal round2(BigDecimal value) {
		return value.setScale(2, RoundingMode.HALF_UP);
	}

	public static BigDecimal add(BigDecimal a, BigDecimal b) {
		return a.add(b);
	}

	public static BigDecimal sub(BigDecimal a, BigDecimal b) {
		return a.subtract(b);
	}

	public static BigDecimal mul(BigDecimal a, int qty) {
		return a.multiply(BigDecimal.valueOf(qty));
	}

	public static BigDecimal min(BigDecimal a, BigDecimal b) {
		return a.min(b);
	}

	public static BigDecimal max(BigDecimal a, BigDecimal b) {
		return a.max(b);
	}
}

