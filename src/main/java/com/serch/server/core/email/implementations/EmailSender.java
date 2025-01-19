package com.serch.server.core.email.implementations;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.serch.server.core.email.EmailService;
import com.serch.server.core.email.templates.EmailTemplateService;
import com.serch.server.exceptions.others.EmailException;
import com.serch.server.models.email.SendEmail;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final Resend resend;
    private final EmailTemplateService service;

    public void send(SendEmail email) {
        log.info(String.format("SERCH::: Loading email configurations - %s", email.getType()));
        CreateEmailOptions params = CreateEmailOptions.builder().build();

        switch (email.getType()) {
            case ADMIN_INVITE: {
                params = CreateEmailOptions.builder()
                        .from("Serch <noreply@notify.serchservice.com>")
                        .to(email.getTo())
                        .subject("Serch | You were invited!")
                        .html(service.adminInvite(email.getFirstName(), email.getPrimary(), email.getContent()))
                        .build();
            }
            break;
            case ADMIN_SIGNUP: {
                params = CreateEmailOptions.builder()
                        .from("Serch <noreply@notify.serchservice.com>")
                        .to(email.getTo())
                        .subject("Serch | Complete your account creation")
                        .html(service.adminSignup(email.getFirstName(), email.getContent()))
                        .build();
            }
            break;
            case ADMIN_LOGIN: {
                params = CreateEmailOptions.builder()
                        .from("Serch <noreply@notify.serchservice.com>")
                        .to(email.getTo())
                        .subject("Serch | Verify your login attempt")
                        .html(service.adminLogin(email.getFirstName(), email.getContent()))
                        .build();
            }
            break;
            case ADMIN_RESET_PASSWORD: {
                params = CreateEmailOptions.builder()
                        .from("Serch <noreply@notify.serchservice.com>")
                        .to(email.getTo())
                        .subject("Serch | Reset your password")
                        .html(service.adminResetPassword(email.getFirstName(), email.getContent()))
                        .build();
            }
            break;
            case ASSOCIATE_INVITE: {
                params = CreateEmailOptions.builder()
                        .from("Serch <noreply@notify.serchservice.com>")
                        .to(email.getTo())
                        .subject("Serch | You've been invited!")
                        .html(service.associateInvite(
                                email.getFirstName(), email.getPrimary(),
                                email.getSecondary(), email.getBusinessLogo(),
                                email.getBusinessDescription(), email.getBusinessCategory(), email.getContent()
                        ))
                        .build();
            }
            break;
            case RESET_PASSWORD: {
                params = CreateEmailOptions.builder()
                        .from("Serch <noreply@notify.serchservice.com>")
                        .to(email.getTo())
                        .subject("Serch | Confirm password reset attempt")
                        .html(service.resetPassword(email.getContent()))
                        .build();
            }
            break;
            case SIGNUP: {
                params = CreateEmailOptions.builder()
                        .from("Serch <noreply@notify.serchservice.com>")
                        .to(email.getTo())
                        .subject("Serch | Verify your identity")
                        .html(service.signup(email.getContent()))
                        .build();
            }
            break;
        }

        try {
            CreateEmailResponse data = resend.emails().send(params);
            log.info(String.format("SERCH::: Processing from email sender - %s", data.getId()));
        } catch (ResendException e) {
            throw new EmailException(e.getMessage());
        }
    }
}