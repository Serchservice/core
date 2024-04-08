package com.serch.server.exceptions.others;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}