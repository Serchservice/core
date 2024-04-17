package com.serch.server.exceptions.others;

/**
 * The TripException class represents an exception related to trip operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class TripException extends RuntimeException {
    public TripException(String message) {
        super(message);
    }
}
