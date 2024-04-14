package com.serch.server.exceptions.auth;

import com.serch.server.exceptions.ExceptionCodes;
import lombok.Getter;

/**
 * The SessionException class represents an exception related to session operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
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
