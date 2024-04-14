package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestPasswordChange;
import com.serch.server.services.auth.requests.RequestResetPassword;
import com.serch.server.services.auth.requests.RequestResetPasswordVerify;
import com.serch.server.services.auth.responses.AuthResponse;

/**
 * Service interface for managing password reset operations.
 *
 * @see com.serch.server.services.auth.services.implementations.ResetPasswordImplementation
 */
public interface ResetPasswordService {

    /**
     * Checks the provided email address for initiating the password reset process.
     *
     * @param emailAddress The email address to check for password reset.
     * @return ApiResponse indicating the status of the email check process.
     *
     * @see ApiResponse
     */
    ApiResponse<String> checkEmail(String emailAddress);

    /**
     * Verifies the provided reset token for resetting the password.
     *
     * @param verify The request containing token verification details.
     * @return ApiResponse indicating the status of the token verification process.
     *
     * @see RequestResetPasswordVerify
     * @see ApiResponse
     */
    ApiResponse<String> verifyToken(RequestResetPasswordVerify verify);

    /**
     * Resets the password based on the provided request.
     *
     * @param resetPassword The request containing password reset details.
     * @return ApiResponse indicating the status of the password reset process.
     *
     * @see RequestResetPassword
     * @see ApiResponse
     */
    ApiResponse<String> resetPassword(RequestResetPassword resetPassword);

    /**
     * Changes the password based on the provided request.
     *
     * @param request The request containing password change details.
     * @return ApiResponse containing the authentication response after password change.
     *
     * @see RequestPasswordChange
     * @see AuthResponse
     * @see ApiResponse
     */
    ApiResponse<AuthResponse> changePassword(RequestPasswordChange request);
}