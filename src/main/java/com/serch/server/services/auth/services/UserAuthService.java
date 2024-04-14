package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.auth.responses.AuthResponse;

import java.time.LocalDateTime;

/**
 * Service interface for managing user authentication.
 *
 * @see com.serch.server.services.auth.services.implementations.UserAuthImplementation
 */
public interface UserAuthService {

    /**
     * Authenticates a user based on login credentials.
     *
     * @param request The login request.
     * @return ApiResponse containing the authentication response.
     *
     * @see RequestLogin
     * @see AuthResponse
     * @see ApiResponse
     */
    ApiResponse<AuthResponse> login(RequestLogin request);

    /**
     * Initiates the process for a user to become a provider.
     *
     * @param request The login request.
     * @return ApiResponse containing the authentication response.
     *
     * @see RequestLogin
     * @see AuthResponse
     * @see ApiResponse
     */
    ApiResponse<AuthResponse> becomeAUser(RequestLogin request);

    /**
     * Registers a new user.
     *
     * @param request The profile request.
     * @return ApiResponse containing the authentication response.
     *
     * @see RequestProfile
     * @see AuthResponse
     * @see ApiResponse
     */
    ApiResponse<AuthResponse> signup(RequestProfile request);

    /**
     * Creates a new user entity based on the provided profile and confirmation time.
     *
     * @param profile      The profile details of the new user.
     * @param confirmedAt  The time when the email address was confirmed.
     * @return The newly created user entity.
     *
     * @see RequestProfile
     * @see User
     */
    User getNewUser(RequestProfile profile, LocalDateTime confirmedAt);

    /**
     * Generates an authentication response for the provided user profile and authentication request.
     *
     * @param request  The authentication request containing user profile details.
     * @param newUser  The newly created user entity.
     * @return ApiResponse containing the authentication response.
     *
     * @see RequestProfile
     * @see User
     * @see AuthResponse
     * @see ApiResponse
     */
    ApiResponse<AuthResponse> getAuthResponse(RequestProfile request, User newUser);
}