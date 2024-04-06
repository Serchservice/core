package com.serch.server.exceptions.auth;

import com.serch.server.exceptions.ExceptionCodes;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final String code;
    public AuthException(String message, String code) {
        super(message);
        this.code = code;
    }

    public AuthException(String message) {
        super(message);
        this.code = ExceptionCodes.UNKNOWN;
    }
}
