package com.serch.server.exceptions.subscription;

/**
 * The SubscriptionException class represents an exception related to subscription operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class SubscriptionException extends RuntimeException {
    public SubscriptionException(String message) {
        super(message);
    }
}