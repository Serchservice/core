package com.serch.server.exceptions.others;

import com.serch.server.exceptions.ExceptionCodes;
import lombok.Getter;

/**
 * The SharedException class represents an exception related to shared operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
@Getter
public class SharedException extends RuntimeException {
    private final String code;
    public SharedException(String message, String code) {
        super(message);
        this.code = code;
    }
    public SharedException(String message) {
        super(message);
        this.code = ExceptionCodes.UNKNOWN;
    }
}
