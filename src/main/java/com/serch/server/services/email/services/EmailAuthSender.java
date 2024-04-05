package com.serch.server.services.email.services;

import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.services.email.models.Email;
import com.serch.server.services.email.template.EmailAuthTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAuthSender implements EmailAuthService {
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
}
