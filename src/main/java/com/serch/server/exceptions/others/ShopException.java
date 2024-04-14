package com.serch.server.exceptions.others;

/**
 * The ShopException class represents an exception related to shop operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class ShopException extends RuntimeException {
    public ShopException(String message) {
        super(message);
    }
}
