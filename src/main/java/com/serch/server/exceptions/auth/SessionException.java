package com.serch.server.exceptions.auth;

import com.serch.server.exceptions.ExceptionCodes;
import lombok.Getter;

@Getter
public class SessionException extends RuntimeException {
    private final String code;
    public SessionException(String message, String code) {
        super(message);
        this.code = code;
    }

    public SessionException(String message) {
        super(message);
        this.code = ExceptionCodes.UNKNOWN;
    }
}
