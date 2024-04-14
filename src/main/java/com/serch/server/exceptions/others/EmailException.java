package com.serch.server.exceptions.others;

/**
 * The EmailException class represents an exception related to email operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class EmailException extends RuntimeException {
    public EmailException(String message) {
        super(message);
    }
}