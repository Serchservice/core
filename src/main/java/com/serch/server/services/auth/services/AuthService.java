package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.services.auth.requests.RequestEmailToken;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.responses.AuthResponse;
import jakarta.validation.constraints.NotNull;

/**
 * Service interface for managing authentication-related operations.
 *
 * @see com.serch.server.services.auth.services.implementations.AuthImplementation
 */
public interface AuthService {

    /**
     * Sends an OTP to the provided email address.
     *
     * @param emailAddress The email address to send the OTP.
     * @return The incomplete user entity.
     *
     * @see Incomplete
     */
    Incomplete sendOtp(String emailAddress);

    /**
     * Checks if the provided email address is valid.
     *
     * @param email The email address to check.
     * @return ApiResponse indicating the result of the check.
     *
     * @see ApiResponse
     */
    ApiResponse<String> checkEmail(String email);

    /**
     * Verifies the OTP sent to the email address.
     *
     * @param request The RequestEmailToken containing the email address and OTP.
     * @return ApiResponse indicating the result of the verification.
     *
     * @see ApiResponse
     * @see RequestEmailToken
     */
    ApiResponse<String> verifyEmailOtp(@NotNull RequestEmailToken request);

    /**
     * Authenticates the user with the provided credentials.
     *
     * @param request The RequestLogin containing the login credentials.
     * @param user    The authenticated user entity.
     * @return ApiResponse containing the authentication response.
     *
     * @see ApiResponse
     * @see RequestLogin
     * @see User
     */
    ApiResponse<AuthResponse> authenticate(RequestLogin request, User user);

    /**
     * Gets a user entity from the incomplete user data.
     *
     * @param incomplete The incomplete user entity.
     * @param role       The role of the user.
     * @return The fully constructed User entity.
     *
     * @see User
     * @see Incomplete
     * @see Role
     */
    User getUserFromIncomplete(Incomplete incomplete, Role role);
}