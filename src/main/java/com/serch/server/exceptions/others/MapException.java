package com.serch.server.exceptions.others;

/**
 * The MapException class represents an exception related to map operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class MapException extends RuntimeException {
    public MapException(String message) {
        super(message);
    }
}
