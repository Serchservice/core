package com.serch.server.exceptions.auth;

import com.serch.server.exceptions.ExceptionCodes;
import lombok.Getter;

/**
 * The AuthException class represents an exception related to auth operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
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
