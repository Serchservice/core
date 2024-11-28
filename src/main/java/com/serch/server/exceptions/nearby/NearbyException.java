package com.serch.server.exceptions.nearby;

/**
 * The NearbyException class represents an exception related to nearby operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class NearbyException extends RuntimeException {
    public NearbyException(String message) {
        super(message);
    }
}
