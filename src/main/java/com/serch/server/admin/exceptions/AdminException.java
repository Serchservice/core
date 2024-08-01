package com.serch.server.admin.exceptions;

/**
 * The AdminException class represents an exception related to admin operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class AdminException extends RuntimeException {
    public AdminException(String message) {
        super(message);
    }
}