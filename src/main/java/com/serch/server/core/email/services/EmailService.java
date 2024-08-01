package com.serch.server.core.email.services;

import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;

/**
 * Service interface for managing email sending operations.
 *
 * @see EmailSender
 */
public interface EmailService {

    /**
     * Sends an email.
     *
     * @param from    Email Address of the sender.
     * @param to      Email Address of the receiver.
     * @param subject Email Subject.
     * @param content Email content, which can be HTML.
     * @param isHTML  Whether the email content is HTML or not.
     * @return ApiResponse containing the response from the email sending operation.
     *
     * @see ApiResponse
     */
    ApiResponse<SendEmailResponse> send(
            String from, String to,
            String subject, String content,
            boolean isHTML
    );
}