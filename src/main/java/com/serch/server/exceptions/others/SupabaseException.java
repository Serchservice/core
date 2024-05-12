package com.serch.server.exceptions.others;

/**
 * The StorageException class represents an exception related to storage operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class SupabaseException extends RuntimeException {
    public SupabaseException(String message) {
        super(message);
    }
}
