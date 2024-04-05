package com.serch.server.services.email.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.EmailException;
import com.serch.server.services.email.models.SendEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSender implements EmailService {
    @Value("${serch.mail-api-key}")
    private String API_KEY;

    private final EmailAuthService emailAuthService;

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

            SendEmailResponse response = resend.emails().send(isHTML
                    ? htmlContent(from, to, subject, content)
                    : textContent(from, to, subject, content)
            );
            return new ApiResponse<>(response);
        } catch (ResendException e) {
            throw new EmailException(e.getMessage());
        }
    }

    @Override
    public ApiResponse<SendEmailResponse> send(SendEmail email) {
        return switch (email.getType()) {
            case SIGNUP -> emailAuthService.sendSignup(email.getTo(), email.getContent());
            case RESET -> emailAuthService.sendReset(email.getTo(), email.getFirstName(), email.getContent());
        };
    }
}