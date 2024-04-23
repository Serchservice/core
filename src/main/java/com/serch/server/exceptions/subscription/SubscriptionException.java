package com.serch.server.exceptions.subscription;

import lombok.Getter;
import lombok.Setter;

/**
 * The SubscriptionException class represents an exception related to subscription operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
@Getter
@Setter
public class SubscriptionException extends RuntimeException {
    private Object data;

    public SubscriptionException(String message) {
        super(message);
    }

    public SubscriptionException(String message, Object data) {
        super(message);
    }
}