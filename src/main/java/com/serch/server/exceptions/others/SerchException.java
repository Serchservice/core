package com.serch.server.exceptions.others;

/**
 * The SerchException class represents an exception related to any other Serch operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class SerchException extends RuntimeException {
    public SerchException(String message) {
        super(message);
    }
}
