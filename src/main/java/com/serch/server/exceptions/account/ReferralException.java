package com.serch.server.exceptions.account;

/**
 * The ReferralException class represents an exception related to referral operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class ReferralException extends RuntimeException {
    public ReferralException(String message) {
        super(message);
    }
}
