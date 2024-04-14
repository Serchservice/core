package com.serch.server.exceptions.subscription;

/**
 * The PlanException class represents an exception related to plan operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class PlanException extends RuntimeException {
    public PlanException(String message) {
        super(message);
    }
}