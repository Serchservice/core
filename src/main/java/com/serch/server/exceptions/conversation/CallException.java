package com.serch.server.exceptions.conversation;

public class CallException extends RuntimeException {
    public CallException(String message) {
        super(message);
    }
}