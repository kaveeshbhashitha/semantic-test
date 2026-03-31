package com.semantic.semantic_test.error;

public class InvalidCouponException extends RuntimeException {
	private final String couponCode;
	private final String reason;

	public InvalidCouponException(String couponCode, String reason) {
		super("Invalid coupon: " + couponCode);
		this.couponCode = couponCode;
		this.reason = reason;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public String getReason() {
		return reason;
	}
}

