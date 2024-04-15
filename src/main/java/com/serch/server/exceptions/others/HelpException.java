package com.serch.server.exceptions.others;

/**
 * The HelpException class represents an exception related to help operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class HelpException extends RuntimeException {
    public HelpException(String message) {
        super(message);
    }
}