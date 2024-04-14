package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestAuth;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.responses.AuthResponse;

/**
 * Service interface for managing business authentication operations.
 */
public interface BusinessAuthService {

    /**
     * Logs in a business user.
     *
     * @param request The login request.
     * @return ApiResponse containing the authentication response.
     *
     * @see ApiResponse
     * @see AuthResponse
     * @see RequestLogin
     */
    ApiResponse<AuthResponse> login(RequestLogin request);

    /**
     * Signs up a business user.
     *
     * @param auth The authentication request.
     * @return ApiResponse containing the authentication response.
     *
     * @see ApiResponse
     * @see AuthResponse
     * @see RequestAuth
     */
    ApiResponse<AuthResponse> signup(RequestAuth auth);

    /**
     * Finishes the signup process for an associate provider.
     *
     * @param auth The authentication request.
     * @return ApiResponse containing the authentication response.
     *
     * @see AuthResponse
     * @see ApiResponse
     * @see RequestAuth
     */
    ApiResponse<AuthResponse> finishAssociateSignup(RequestAuth auth);
}