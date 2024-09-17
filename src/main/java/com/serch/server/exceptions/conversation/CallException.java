package com.serch.server.exceptions.conversation;

import com.serch.server.exceptions.ExceptionCodes;
import lombok.Getter;

/**
 * The CallException class represents an exception related to call operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
@Getter
public class CallException extends RuntimeException {
    private String code;

    public CallException(String message, boolean withCode) {
        super(message);

        if(withCode) {
            this.code = ExceptionCodes.CALL_ERROR;
        }
    }
}