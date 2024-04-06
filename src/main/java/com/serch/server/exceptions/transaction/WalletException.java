package com.serch.server.exceptions.transaction;

public class WalletException extends RuntimeException {
    public WalletException(String message) {
        super(message);
    }
}
