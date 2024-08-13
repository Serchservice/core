package com.serch.server.core.email;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.Recipient;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.serch.server.enums.email.EmailType;
import com.serch.server.exceptions.others.EmailException;
import com.serch.server.models.email.SendEmail;
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
    private final MailerSend send;

    @Value("${spring.mail.username}")
    private String MAIL_SENDER;

    @Value("${application.mail.template.id.auth}")
    private String MAIL_AUTH_TEMPLATE_ID;

    @Value("${application.mail.template.id.reset}")
    private String MAIL_RESET_TEMPLATE_ID;

    @Value("${application.mail.template.id.invite}")
    private String MAIL_INVITE_TEMPLATE_ID;

    private String buildHeader(EmailType type) {
        return switch(type) {
            case SIGNUP, ADMIN_SIGNUP -> "Complete your verification";
            case ADMIN_LOGIN -> "Complete your login";
            default -> "";
        };
    }

    private String buildBody(EmailType type, String primary, String secondary) {
        return switch (type) {
            case SIGNUP -> "You've initiated the email verification, which is the first step to comfort and earning extra cash in Serch. As a means of verifying your identity, use the one-time password below to verify your email address.";
            case ADMIN_LOGIN -> "It seems like you are trying to login to your admin account. Verify that you are the one who made this request with the one-time password below.";
            case ADMIN_SIGNUP -> "Welcome to Serch Administrative block. Seems like your email address was used to create an admin account. Use the one-time password below to verify that you made this action happen.";
            case ASSOCIATE_INVITE -> String.format(
                    "The business organization, %s owned by %s, has invited you as an associate provider within its organization in the Serch platform. Click the link below to finish up with your account setup and start rendering your services with comfort and security.",
                    primary, secondary
            );
            case ADMIN_INVITE -> String.format(
                    "The Serchservice administrator, %s - %s, has invited you as an admin within the Serch platform. Click the link below to finish up with your account setup and continue being awesome! We cannot wait to see the wonders you will do.",
                    primary, secondary
            );
            case ADMIN_RESET_PASSWORD -> "It seems like you forgot your password, which is something that can happen to anyone. But for security purposes, you need to verify that your identity. So, you can click the link below to reset your password, if you requested for this.";
            default -> "";
        };
    }

    public void send(SendEmail email) {
        try {
            Email mail = new Email();

            mail.setFrom("Team Serch", MAIL_SENDER);

            Recipient recipient = new Recipient(email.getFirstName(), email.getTo());
            mail.AddRecipient(recipient);

            switch (email.getType()) {
                case SIGNUP, ADMIN_SIGNUP, ADMIN_LOGIN: {
                    mail.setTemplateId(MAIL_AUTH_TEMPLATE_ID);
                    mail.addPersonalization(recipient, "body", buildBody(email.getType(), null, null));
                    mail.addPersonalization(recipient, "code", email.getContent());
                    mail.addPersonalization(recipient, "header", buildHeader(email.getType()));
                }
                break;
                case RESET_PASSWORD: {
                    mail.setTemplateId(MAIL_RESET_TEMPLATE_ID);
                    mail.addPersonalization(recipient, "code", email.getContent());
                }
                break;
                case ASSOCIATE_INVITE: {
                    mail.setTemplateId(MAIL_INVITE_TEMPLATE_ID);
                    mail.setSubject("Associate Invitation | You were invited!");
                    mail.addPersonalization(recipient, "body", buildBody(email.getType(), email.getPrimary(), email.getSecondary()));
                    mail.addPersonalization(recipient, "name", email.getFirstName());
                    mail.addPersonalization(recipient, "invite_url", email.getContent());
                    mail.addPersonalization(recipient, "button_text", "Finish account setup");
                }
                break;
                case ADMIN_INVITE: {
                    mail.setTemplateId(MAIL_INVITE_TEMPLATE_ID);
                    mail.setSubject("Serch Invitation | You were invited!");
                    mail.addPersonalization(recipient, "body", buildBody(email.getType(), email.getPrimary(), email.getSecondary()));
                    mail.addPersonalization(recipient, "name", email.getFirstName());
                    mail.addPersonalization(recipient, "invite_url", email.getContent());
                    mail.addPersonalization(recipient, "button_text", "Finish account setup");
                }
                break;
                case ADMIN_RESET_PASSWORD: {
                    mail.setTemplateId(MAIL_INVITE_TEMPLATE_ID);
                    mail.setSubject("Reset Password | Get access to your admin account");
                    mail.addPersonalization(recipient, "body", buildBody(email.getType(), email.getPrimary(), email.getSecondary()));
                    mail.addPersonalization(recipient, "name", email.getFirstName());
                    mail.addPersonalization(recipient, "invite_url", email.getContent());
                    mail.addPersonalization(recipient, "button_text", "Reset my password");
                }
                break;
            }

            MailerSendResponse response = send.emails().send(mail);
            System.out.println(response);
        } catch (MailerSendException e) {
            throw new EmailException(e.getMessage());
        }
    }
}