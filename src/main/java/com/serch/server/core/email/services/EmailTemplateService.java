package com.serch.server.core.email.services;

import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.models.email.Email;
import com.serch.server.models.email.SendEmail;

/**
 * Service interface for sending authentication-related emails.
 */
public interface EmailTemplateService {
    /**
     * Sends an email for signup verification.
     *
     * @param to      Email address receiving the mail.
     * @param content The one-time password for account email confirmation.
     * @return ApiResponse containing the response from the email sending operation.
     */
    ApiResponse<SendEmailResponse> sendSignup(String to, String content);

    /**
     * Sends an email for signup verification.
     *
     * @param to      Email address receiving the mail.
     * @param content The one-time password for account email confirmation.
     * @param name The name of the account holder.
     * @return ApiResponse containing the response from the email sending operation.
     */
    ApiResponse<SendEmailResponse> sendLogin(String to, String content, String name);

    /**
     * Sends an email for password reset verification.
     *
     * @param to        Email address receiving the mail.
     * @param firstName The first name of the recipient.
     * @param content   The one-time password for account password reset.
     * @return ApiResponse containing the response from the email sending operation.
     */
    ApiResponse<SendEmailResponse> sendReset(String to, String firstName, String content);

    /**
     * Sends an email for unsuccessful payment.
     *
     * @param to        Email address receiving the mail.
     * @param firstName The first name of the recipient.
     * @return ApiResponse containing the response from the email sending operation.
     */
    ApiResponse<SendEmailResponse> sendUnsuccessful(String to, String firstName);

    /**
     * Sends the email based on the provided SendEmail object.
     *
     * @param email SendEmail object containing email details.
     * @return ApiResponse containing the response from the email sending operation.
     */
    ApiResponse<SendEmailResponse> send(SendEmail email);

    /**
     * Send email with {@link Email}
     *
     * @param email The email content
     */
    void sendEmail(Email email);
}
