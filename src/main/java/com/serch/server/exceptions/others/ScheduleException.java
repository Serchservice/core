package com.serch.server.exceptions.others;

/**
 * The ScheduleException class represents an exception related to schedule operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class ScheduleException extends RuntimeException {
    public ScheduleException(String message) {
        super(message);
    }
}
