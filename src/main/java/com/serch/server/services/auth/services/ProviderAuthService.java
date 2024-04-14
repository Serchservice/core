package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.services.auth.requests.*;
import com.serch.server.services.auth.responses.AuthResponse;

import java.util.List;

/**
 * Service interface for managing provider authentication and profile management.
 *
 * @see com.serch.server.services.auth.services.implementations.ProviderAuthImplementation
 */
public interface ProviderAuthService {

    /**
     * Authenticates a provider based on login credentials.
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
     * Saves the profile of a provider.
     *
     * @param request The provider profile request.
     * @return ApiResponse indicating the status of the profile saving operation.
     *
     * @see RequestProviderProfile
     * @see ApiResponse
     */
    ApiResponse<String> saveProfile(RequestProviderProfile request);

    /**
     * Saves a category for the provider's services.
     *
     * @param category The request containing the category details.
     * @return ApiResponse indicating the status of the category saving operation.
     *
     * @see RequestSerchCategory
     * @see ApiResponse
     */
    ApiResponse<String> saveCategory(RequestSerchCategory category);

    /**
     * Saves specialties for the provider.
     *
     * @param specialty The request containing the specialty details.
     * @return ApiResponse indicating the status of the specialties saving operation.
     *
     * @see RequestAuthSpecialty
     * @see ApiResponse
     */
    ApiResponse<String> saveSpecialties(RequestAuthSpecialty specialty);

    /**
     * Saves additional information for the provider.
     *
     * @param request The request containing the additional information.
     * @return ApiResponse indicating the status of the additional information saving operation.
     *
     * @see RequestAdditionalInformation
     * @see ApiResponse
     */
    ApiResponse<String> saveAdditional(RequestAdditionalInformation request);

    /**
     * Checks the status of a provider based on email address.
     *
     * @param emailAddress The email address of the provider.
     * @return ApiResponse containing the status information.
     *
     * @see ApiResponse
     */
    ApiResponse<String> checkStatus(String emailAddress);

    /**
     * Finishes the signup process for a provider.
     *
     * @param auth The authentication request containing signup details.
     * @return ApiResponse containing the authentication response.
     *
     * @see RequestAuth
     * @see AuthResponse
     * @see ApiResponse
     */
    ApiResponse<AuthResponse> finishSignup(RequestAuth auth);

    // Methods below are package-private and intended for internal use within the service implementation

    void addSpecialtiesToIncompleteProfile(List<Long> specialties, Incomplete incomplete);

    void saveCategory(SerchCategory category, Incomplete incomplete);

    void saveProfile(RequestProviderProfile request, Incomplete incomplete);

    void saveReferral(String code, Incomplete incomplete);
}