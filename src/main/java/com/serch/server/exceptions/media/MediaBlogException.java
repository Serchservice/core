package com.serch.server.exceptions.media;

/**
 * The MediaBlogException class represents an exception related to media blog operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class MediaBlogException extends RuntimeException {
    public MediaBlogException(String message) {
        super(message);
    }
}