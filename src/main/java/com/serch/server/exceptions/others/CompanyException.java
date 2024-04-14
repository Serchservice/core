package com.serch.server.exceptions.others;

/**
 * The CompanyException class represents an exception related to company operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class CompanyException extends RuntimeException {
    public CompanyException(String message) {
        super(message);
    }
}
