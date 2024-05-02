package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.services.account.responses.AdditionalInformationResponse;
import com.serch.server.services.auth.requests.RequestAdditionalInformation;

/**
 * The AdditionalService interface provides methods for managing additional profile information.
 *
 * @see com.serch.server.services.account.services.implementations.AdditionalImplementation
 */
public interface AdditionalService {

    /**
     * Saves additional information provided by the user.
     * @param additional The additional profile information.
     * @param profile The user's profile.
     *
     * @see RequestAdditionalInformation
     * @see Profile
     */
    void createAdditional(RequestAdditionalInformation additional, Profile profile);

    /**
     * Retrieves and returns the additional profile information of the current user.
     * @return ApiResponse containing the additional information response.
     *
     * @see ApiResponse
     * @see AdditionalInformationResponse
     */
    ApiResponse<AdditionalInformationResponse> view();
}

