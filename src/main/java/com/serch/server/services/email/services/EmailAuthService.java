package com.serch.server.services.email.services;

import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.models.email.SendEmail;

public interface EmailAuthService {
    /**
     * @param to Email Address receiving the mail
     * @param content The OTP for account email confirmation
     *
     * @return ApiResponse of SendEmailResponse
     */
    ApiResponse<SendEmailResponse> sendSignup(String to, String content);

    /**
     * @param to Email Address receiving the mail
     * @param firstName The first name of the recipient
     * @param content The OTP for account password reset
     *
     * @return ApiResponse of SendEmailResponse
     */
    ApiResponse<SendEmailResponse> sendReset(String to, String firstName, String content);

    /**
     * @param email SendEmail content
     *
     * @return ApiResponse of SendEmailResponse
     */
    ApiResponse<SendEmailResponse> send(SendEmail email);
}
