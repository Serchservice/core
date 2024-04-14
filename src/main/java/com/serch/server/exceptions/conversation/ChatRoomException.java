package com.serch.server.exceptions.conversation;

/**
 * The ChatRoomException class represents an exception related to chat room operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class ChatRoomException extends RuntimeException {
    public ChatRoomException(String message) {
        super(message);
    }
}