package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.services.account.requests.UpdateProfileRequest;
import com.serch.server.services.account.responses.BusinessProfileResponse;
import com.serch.server.services.account.responses.ProfileResponse;

import java.util.List;

/**
 * Service interface for managing business profiles, including creation, updating, and retrieval.
 *
 * @see com.serch.server.services.account.services.implementations.BusinessImplementation
 */
public interface BusinessService {
    /**
     * Creates a business profile based on incomplete information and a user.
     *
     * @param incomplete Incomplete information about the business.
     * @param user       The user for whom the profile is being created.
     * @return An ApiResponse indicating the success of the operation.
     *
     * @see Incomplete
     * @see User
     * @see ApiResponse
     */
    ApiResponse<String> createProfile(Incomplete incomplete, User user);

    /**
     * Retrieves profiles of associates with the business.
     *
     * @return An ApiResponse containing a list of associate profiles.
     *
     * @see ProfileResponse
     * @see ApiResponse
     */
    ApiResponse<List<ProfileResponse>> associates();

    /**
     * Retrieves profiles of subscribed associates with the business.
     *
     * @return An ApiResponse containing a list of associate profiles.
     *
     * @see ProfileResponse
     * @see ApiResponse
     */
    ApiResponse<List<ProfileResponse>> subscribedAssociates();

    /**
     * Retrieves the business profile of the logged-in user.
     *
     * @return An ApiResponse containing the business profile.
     *
     * @see ApiResponse
     * @see BusinessProfileResponse
     */
    ApiResponse<BusinessProfileResponse> profile();

    /**
     * Updates the business profile based on the provided request.
     *
     * @param request The request containing updated profile information.
     * @return An ApiResponse indicating the success of the update.
     *
     * @see ApiResponse
     * @see UpdateProfileRequest
     */
    ApiResponse<String> update(UpdateProfileRequest request);
}