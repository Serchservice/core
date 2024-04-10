package com.serch.server.services.email.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.EmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSender implements EmailService {
    @Value("${spring.mail.password}")
    private String API_KEY;

    private SendEmailRequest htmlContent(String from, String to, String subject, String content) {
        return SendEmailRequest.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .html(content)
                .build();
    }

    private SendEmailRequest textContent(String from, String to, String subject, String content) {
        return SendEmailRequest.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .text(content)
                .build();
    }

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