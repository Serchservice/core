package com.serch.server.exceptions.media;

/**
 * The MediaLegalException class represents an exception related to media legal operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class MediaLegalException extends RuntimeException {
    public MediaLegalException(String message) {
        super(message);
    }
}