package com.serch.server.exceptions.media;

/**
 * The MediaAssetException class represents an exception related to media asset operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class MediaAssetException extends RuntimeException {
    public MediaAssetException(String message) {
        super(message);
    }
}