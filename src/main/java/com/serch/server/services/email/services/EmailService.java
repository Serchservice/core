package com.serch.server.services.email.services;

import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;

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
}