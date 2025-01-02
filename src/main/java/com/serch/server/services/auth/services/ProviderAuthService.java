package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestAdditionalInformation;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.responses.AuthResponse;

/**
 * Service interface for managing provider authentication and profile management.
 *
 * @see com.serch.server.services.auth.services.implementations.ProviderAuthImplementation
 */
public interface ProviderAuthService {

    /**
     * Authenticates a provider based on login credentials.
     *
     * @param request The {@link RequestLogin} request.
     * @return ApiResponse containing the authentication response.
     *
     * @see RequestLogin
     * @see AuthResponse
     * @see ApiResponse
     */
    ApiResponse<AuthResponse> login(RequestLogin request);

    /**
     * Saves additional information for the provider and finishes the signup process for a provider.
     *
     * @param request The request containing the additional information.
     * @return ApiResponse indicating the status of the additional information saving operation.
     *
     * @see RequestAdditionalInformation
     * @see AuthResponse
     * @see ApiResponse
     */
    ApiResponse<AuthResponse> signup(RequestAdditionalInformation request);
}