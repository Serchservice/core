package com.serch.server.services.email.services;

import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.models.email.Email;
import com.serch.server.models.email.SendEmail;
import com.serch.server.services.email.template.EmailAuthTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementation for sending authentication-related emails.
 * It implements the {@link EmailTemplateService} interface.
 *
 * @see EmailTemplateService
 */
@Service
@RequiredArgsConstructor
public class EmailTemplateSender implements EmailTemplateService {
    private final EmailService service;

    @Override
    public ApiResponse<SendEmailResponse> sendSignup(String to, String content) {
        Email email = new Email();
        email.setEmailAddress(to);
        email.setOtp(content);
        email.setCentered(false);
        email.setContent(
                "Thanks for initiating an account creating session on the Serch Platform." +
                        " As a means of verifying your identity, use the one-time password below to verify" +
                        " your email address."
        );
        email.setGreeting("Hey there");
        email.setImageHeader("/messages.png?alt=media&token=2c2e888d-67ad-46e4-b74e-ff62f21c2683");

        return service.send(
                "team@serchservice.com",
                to,
                "Create Account - Verify your email address",
                EmailAuthTemplate.email(email),
                true
        );
    }

    @Override
    public ApiResponse<SendEmailResponse> sendReset(String to, String firstName, String content) {
        Email email = new Email();
        email.setEmailAddress(to);
        email.setOtp(content);
        email.setCentered(true);
        email.setContent(
                "Seems like you forgot your password and requested for password reset." +
                        " As a means of verifying your identity, use the one-time password below to verify" +
                        " your email address."
        );
        email.setGreeting("Hi %s".formatted(firstName));
        email.setImageHeader("/security.png?alt=media&token=9b7ce5b9-6353-4da9-b423-e73bf153eee3");

        return service.send(
                "team@serchservice.com",
                to,
                "Reset Password - Verify your email address",
                EmailAuthTemplate.email(email),
                true
        );
    }

    @Override
    public ApiResponse<SendEmailResponse> sendUnsuccessful(String to, String firstName) {
        Email email = new Email();
        email.setEmailAddress(to);
        email.setCentered(true);
        email.setContent(
                "Heads up, there was a problem processing your payment. No need to worry, things happen. " +
                        "It's easy to fix - you'll be earning again in no time."
        );
        email.setGreeting("Hi %s".formatted(firstName));
        email.setImageHeader("/security.png?alt=media&token=9b7ce5b9-6353-4da9-b423-e73bf153eee3");

        return service.send(
                "team@serchservice.com",
                to,
                "Your payment was unsuccessful",
                EmailAuthTemplate.email(email),
                true
        );
    }

    @Override
    public ApiResponse<SendEmailResponse> send(SendEmail email) {
        return switch (email.getType()) {
            case SIGNUP -> sendSignup(email.getTo(), email.getContent());
            case RESET -> sendReset(email.getTo(), email.getFirstName(), email.getContent());
            case UNSUCCESSFUL_PAYMENT -> sendUnsuccessful(email.getTo(), email.getFirstName());
        };
    }
}