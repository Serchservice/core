package com.serch.server.exceptions.conversation;

/**
 * The CallException class represents an exception related to call operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class CallException extends RuntimeException {
    public CallException(String message) {
        super(message);
    }
}