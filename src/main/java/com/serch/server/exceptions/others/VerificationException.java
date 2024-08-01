package com.serch.server.exceptions.others;

/**
 * The VerificationException class represents an exception related to verification operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class VerificationException extends RuntimeException {
    public VerificationException(String message) {
        super(message);
    }
}
