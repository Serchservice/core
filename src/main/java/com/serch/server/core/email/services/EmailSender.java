package com.serch.server.core.email.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.EmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service implementation for sending emails.
 * It implements its wrapper interface {@link EmailService}.
 *
 * @see EmailService
 */
@Service
@RequiredArgsConstructor
public class EmailSender implements EmailService {

    @Value("${spring.mail.password}")
    private String API_KEY;

    /**
     * Constructs an HTML email request.
     *
     * @param from    The sender's email address.
     * @param to      The recipient's email address.
     * @param subject The subject of the email.
     * @param content The HTML content of the email.
     * @return A SendEmailRequest object representing the HTML email.
     */
    private SendEmailRequest htmlContent(String from, String to, String subject, String content) {
        return SendEmailRequest.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .html(content)
                .build();
    }

    /**
     * Constructs a plain text email request.
     *
     * @param from    The sender's email address.
     * @param to      The recipient's email address.
     * @param subject The subject of the email.
     * @param content The text content of the email.
     * @return A SendEmailRequest object representing the plain text email.
     */
    private SendEmailRequest textContent(String from, String to, String subject, String content) {
        return SendEmailRequest.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .text(content)
                .build();
    }

    /**
     * Sends an email.
     *
     * @param from     The sender's email address.
     * @param to       The recipient's email address.
     * @param subject  The subject of the email.
     * @param content  The content of the email.
     * @param isHTML   Indicates whether the email content is HTML.
     * @return ApiResponse containing the response from the email sending operation.
     *
     * @throws EmailException if an error occurs during email sending.
     */
    @Override
    public ApiResponse<SendEmailResponse> send(
            String from, String to,
            String subject, String content,
            boolean isHTML
    ) {
        try {
            Resend resend = new Resend(API_KEY);
            return switch (String.valueOf(isHTML).toLowerCase()) {
                case "true" -> new ApiResponse<>(resend.emails().send(htmlContent(from, to, subject, content)));
                case "false" -> new ApiResponse<>(resend.emails().send(textContent(from, to, subject, content)));
                default -> throw new EmailException("Unexpected value: " + String.valueOf(isHTML).toLowerCase());
            };
        } catch (ResendException e) {
            throw new EmailException(e.getMessage());
        }
    }
}