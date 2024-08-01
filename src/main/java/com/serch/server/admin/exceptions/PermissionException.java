package com.serch.server.admin.exceptions;

/**
 * The PermissionException class represents an exception related to permission operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class PermissionException extends RuntimeException {
    public PermissionException(String message) {
        super(message);
    }
}