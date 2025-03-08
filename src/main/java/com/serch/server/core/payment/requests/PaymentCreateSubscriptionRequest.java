package com.serch.server.core.payment.requests;

public record PaymentCreateSubscriptionRequest(String customer, String plan) {
}