package com.serch.server.exceptions.others;

/**
 * The PaymentException class represents an exception related to payment operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}