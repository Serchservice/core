package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.services.account.responses.AdditionalInformationResponse;

/**
 * The AdditionalService interface provides methods for managing additional profile information.
 *
 * @see com.serch.server.services.account.services.implementations.AdditionalImplementation
 */
public interface AdditionalService {

    /**
     * Saves incomplete additional information provided by the user.
     * @param incomplete The incomplete profile information.
     * @param profile The user's profile.
     *
     * @see Incomplete
     * @see Profile
     */
    void saveIncompleteAdditional(Incomplete incomplete, Profile profile);

    /**
     * Retrieves and returns the additional profile information of the current user.
     * @return ApiResponse containing the additional information response.
     *
     * @see ApiResponse
     * @see AdditionalInformationResponse
     */
    ApiResponse<AdditionalInformationResponse> view();
}

