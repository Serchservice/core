package com.serch.server.exceptions.conversation;

import lombok.Getter;

/**
 * The ChatException class represents an exception related to chat operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
@Getter
public class ChatException extends RuntimeException {
    private final String user;
    public ChatException(String message, String user) {
        super(message);
        this.user = user;
    }
}