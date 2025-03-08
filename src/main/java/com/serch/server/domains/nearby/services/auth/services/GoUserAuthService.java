package com.serch.server.domains.nearby.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.auth.responses.GoAuthResponse;
import com.serch.server.domains.nearby.services.auth.requests.GoAuthRequest;
import com.serch.server.domains.nearby.services.auth.requests.GoPasswordRequest;

/**
 * This interface defines the contract for a user authentication service.
 * It provides methods for various authentication and auth operations.
 */
public interface GoUserAuthService {
    /**
     * Logs in a user with the provided credentials.
     *
     * @param request The {@link GoAuthRequest} authentication request containing user credentials.
     * @return An {@link ApiResponse} containing the authentication response, or an error message.
     */
    ApiResponse<GoAuthResponse> login(GoAuthRequest request);

    /**
     * Registers a new user with the provided credentials.
     *
     * @param request The {@link GoAuthRequest} registration request containing user information.
     * @return An {@link ApiResponse} containing the registration response, or an error message.
     */
    ApiResponse<String> signup(GoAuthRequest request);

    /**
     * Verifies the signup token for a user.
     *
     * @param emailAddress The email address of the user.
     * @param token The signup verification token.
     * @return An {@link ApiResponse} containing the verification response, or an error message.
     */
    ApiResponse<GoAuthResponse> verifySignupToken(String emailAddress, String token);

    /**
     * Refreshes the authentication token for an existing user.
     *
     * @param token The existing authentication token.
     * @return An {@link ApiResponse} containing the refreshed authentication token, or an error message.
     */
    ApiResponse<GoAuthResponse> refreshToken(String token);

    /**
     * Initiates the password reset process for a user.
     *
     * @param emailAddress The email address of the user.
     * @return An {@link ApiResponse} containing a success message or an error message.
     */
    ApiResponse<String> forgotPassword(String emailAddress);

    /**
     * Verifies the password reset token for a user.
     *
     * @param emailAddress The email address of the user.
     * @param token The password reset token.
     * @return An {@link ApiResponse} containing a success message or an error message.
     */
    ApiResponse<String> verifyToken(String emailAddress, String token);

    /**
     * Resets the password for a user.
     *
     * @param request The {@link GoAuthRequest} password reset request containing the new password.
     * @return An {@link ApiResponse} containing a success message or an error message.
     */
    ApiResponse<String> resetPassword(GoAuthRequest request);

    /**
     * Resends the verification for a user.
     *
     * @param emailAddress The email address of the user.
     * @param isSignup Determines if the token request is for signup or not.
     * @return An {@link ApiResponse} containing a success message or an error message.
     */
    ApiResponse<String> resend(String emailAddress, Boolean isSignup);

    /**
     * Changes the password of the logged-in user.
     *
     * @param request The {@link GoPasswordRequest} authentication request containing user credentials.
     * @return An {@link ApiResponse} containing the authentication response, or an error message.
     */
    ApiResponse<GoAuthResponse> changePassword(GoPasswordRequest request);
}