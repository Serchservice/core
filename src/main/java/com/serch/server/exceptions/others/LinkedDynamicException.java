package com.serch.server.exceptions.others;

/**
 * The CertificateException class represents an exception related to uploadCertificate operations.
 * It extends the RuntimeException class, indicating that it is an unchecked exception.
 * <p></p>
 * @see RuntimeException
 */
public class LinkedDynamicException extends RuntimeException {
    public LinkedDynamicException(String message) {
        super(message);
    }
}
