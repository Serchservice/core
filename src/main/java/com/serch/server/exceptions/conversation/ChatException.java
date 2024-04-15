package com.serch.server.exceptions.conversation;

/**
 * The ChatException class represents an exception related to chat operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class ChatException extends RuntimeException {
    public ChatException(String message) {
        super(message);
    }
}