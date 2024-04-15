package com.serch.server.exceptions.others;

/**
 * The RatingException class represents an exception related to rating operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class RatingException extends RuntimeException {
    public RatingException(String message) {
        super(message);
    }
}
