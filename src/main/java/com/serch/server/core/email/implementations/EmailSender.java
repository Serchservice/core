package com.serch.server.core.email.implementations;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.serch.server.core.email.EmailService;
import com.serch.server.core.email.EmailTemplateService;
import com.serch.server.core.email.GoEmailTemplateService;
import com.serch.server.domains.nearby.models.go.user.GoUserAddon;
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
    private final GoEmailTemplateService goService;

    public void send(SendEmail email) {
        log.info("SERCH::: Loading email setups - {}", email.getType());
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
                        .from("%s <noreply@notify.serchservice.com>".formatted(email.getIsNearby() ? "Nearby" : "Serch"))
                        .to(email.getTo())
                        .subject("%s | Confirm password reset attempt".formatted(email.getIsNearby() ? "Nearby" : "Serch"))
                        .html(service.resetPassword(email.getContent(), email.getIsNearby()))
                        .build();
            }
            break;
            case SIGNUP: {
                params = CreateEmailOptions.builder()
                        .from("%s <noreply@notify.serchservice.com>".formatted(email.getIsNearby() ? "Nearby" : "Serch"))
                        .to(email.getTo())
                        .subject("%s | Verify your identity".formatted(email.getIsNearby() ? "Nearby" : "Serch"))
                        .html(service.signup(email.getContent(), email.getIsNearby()))
                        .build();
            }
            break;
        }

        send(params);
    }

    private void send(CreateEmailOptions params) {
        try {
            CreateEmailResponse data = resend.emails().send(params);
            log.info("SERCH::: Processing from email sender - {}", data.getId());
        } catch (ResendException e) {
            throw new EmailException(e.getMessage());
        }
    }

    @Override
    public void send(GoUserAddon addon, String extra, boolean isSuccess) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("%s <noreply@notify.serchservice.com>".formatted("Nearby"))
                .to(addon.getUser().getEmailAddress())
                .subject("Nearby | Your %s Addon Charge %s".formatted(addon.getPlan().getName(), isSuccess ? "Succeeded" : "Failed"))
                .html(isSuccess ? goService.success(addon, extra) : goService.failed(addon, extra))
                .build();

        send(params);
    }

    @Override
    public void send(GoUserAddon addon, boolean isSwitch) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("%s <noreply@notify.serchservice.com>".formatted("Nearby"))
                .to(addon.getUser().getEmailAddress())
                .subject("Nearby | Upcoming Charge for Your %s Addon".formatted(addon.getPlan().getName()))
                .html(isSwitch ? goService.invoiceSwitch(addon) : goService.invoice(addon))
                .build();

        send(params);
    }

    @Override
    public void send(GoUserAddon addon, String link) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("%s <noreply@notify.serchservice.com>".formatted("Nearby"))
                .to(addon.getUser().getEmailAddress())
                .subject("Nearby | Complete Your %s Addon Payment".formatted(addon.getPlan().getName()))
                .html(goService.pending(addon, link))
                .build();

        send(params);
    }
}