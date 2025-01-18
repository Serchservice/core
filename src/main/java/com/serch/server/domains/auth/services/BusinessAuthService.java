package com.serch.server.domains.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.auth.requests.RequestBusinessProfile;
import com.serch.server.domains.auth.requests.RequestLogin;
import com.serch.server.domains.auth.responses.AuthResponse;

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
     * @param profile The business profile.
     * @return ApiResponse containing the authentication response.
     *
     * @see ApiResponse
     * @see AuthResponse
     * @see RequestBusinessProfile
     */
    ApiResponse<AuthResponse> signup(RequestBusinessProfile profile);
}