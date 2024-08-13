package com.serch.server.core.email;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.email.SendEmail;

/**
 * Service interface for managing email sending operations.
 *
 * @see EmailSender
 */
public interface EmailService {

    /**
     * Sends an email.
     *
     * @param email    {@link SendEmail} data payload
     *
     * @see ApiResponse
     */
    void send(SendEmail email);
}