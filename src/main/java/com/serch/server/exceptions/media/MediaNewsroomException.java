package com.serch.server.exceptions.media;

/**
 * The MediaNewsroomException class represents an exception related to media newsroom operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class MediaNewsroomException extends RuntimeException {
    public MediaNewsroomException(String message) {
        super(message);
    }
}