package com.serch.server.exceptions.others;

import com.serch.server.exceptions.ExceptionCodes;
import lombok.Getter;

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
