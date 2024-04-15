package com.serch.server.exceptions.account;

/**
 * The AccountException class represents an exception related to account operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class AccountException extends RuntimeException {
    public AccountException(String message) {
        super(message);
    }
}