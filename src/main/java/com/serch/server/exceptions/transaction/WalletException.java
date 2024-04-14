package com.serch.server.exceptions.transaction;

/**
 * The WalletException class represents an exception related to wallet operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class WalletException extends RuntimeException {
    public WalletException(String message) {
        super(message);
    }
}
