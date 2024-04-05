package com.serch.server.services.email.services;

import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.services.email.models.SendEmail;

public interface EmailService {
    /**
     * @param from    Email Address of the sender
     * @param to      Email Address of the receiver
     * @param subject Email Subject
     * @param content Email content - Can be HTML
     * @param isHTML Whether the email is an html content or not
     */
    ApiResponse<SendEmailResponse> send(
            String from, String to,
            String subject, String content,
            boolean isHTML
    );

    /**
     * @param email SendEmail content
     *
     * @return ApiResponse of SendEmailResponse
     */
    ApiResponse<SendEmailResponse> send(SendEmail email);
}