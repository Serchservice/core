package com.serch.server.domains.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.domains.account.requests.UpdateBusinessRequest;
import com.serch.server.domains.account.responses.BusinessProfileResponse;
import com.serch.server.domains.auth.requests.RequestBusinessProfile;
import com.serch.server.domains.account.services.implementations.BusinessImplementation;

/**
 * Service interface for managing business profiles, including creation, updating, and retrieval.
 *
 * @see BusinessImplementation
 */
public interface BusinessService {
    /**
     * Creates a business profile based on incomplete information and a user.
     *
     * @param incomplete Incomplete information about the business.
     * @param user       The user for whom the profile is being created.
     * @param profile The profile of the business
     * @return An ApiResponse indicating the success of the operation.
     *
     * @see Incomplete
     * @see User
     * @see ApiResponse
     * @see RequestBusinessProfile
     */
    ApiResponse<String> createProfile(Incomplete incomplete, User user, RequestBusinessProfile profile);

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
     * @see UpdateBusinessRequest
     * @see BusinessProfileResponse
     */
    ApiResponse<BusinessProfileResponse> update(UpdateBusinessRequest request);
}