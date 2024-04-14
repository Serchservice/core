package com.serch.server.exceptions.others;

/**
 * The StorageException class represents an exception related to storage operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }
}
