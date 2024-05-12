package com.serch.server.services.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedLogin;
import com.serch.server.models.shared.SharedStatus;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.shared.responses.SharedLinkData;
import com.serch.server.services.shared.responses.SharedLinkResponse;
import com.serch.server.services.shared.responses.SharedStatusData;

import java.util.List;

/**
 * Service interface for shared operations.
 *
 * @see com.serch.server.services.shared.services.implementations.SharedImplementation
 */
public interface SharedService {
    /**
     * Retrieves account information based on the provided ID.
     *
     * @param id The ID of the account.
     * @return A response containing the account information.
     */
    ApiResponse<List<AccountResponse>> accounts(String id);

    /**
     * Retrieves a list of guest responses.
     *
     * @return A response containing a list of guest responses.
     */
    ApiResponse<List<SharedLinkResponse>> links();

    /**
     * Gets the current status of the shared link
     *
     * @param link The Shared Link
     *
     * @return {@link SharedStatus}
     */
    SharedStatus getCurrentStatus(SharedLink link);

    /**
     * Prepares SharedLink data response
     *
     * @param link The SharedLink data to prepare data with
     * @param status The SharedLink status
     *
     * @return {@link SharedLinkData}
     */
    SharedLinkData data(SharedLink link, SharedStatus status);

    /**
     * Gets the current status of a particular account in a SharedLink
     *
     * @param login The Login Data
     *
     * @return The current SharedStatus
     */
    SharedStatus getCurrentStatusForAccount(SharedLogin login);

    /**
     * Prepares the SharedStatusData response from {@link SharedLink} and {@link SharedStatus}
     *
     * @param link The SharedLink data
     * @param status The SharedStatus data
     *
     * @return SharedPricingData response {@link SharedStatusData}
     */
    SharedStatusData getStatusData(SharedLink link, SharedStatus status);

    /**
     * Builds an ApiResponse containing the account response for a user.
     * @param guest The guest entity associated with the user.
     * @param user The user entity.
     * @return ApiResponse containing the account response.
     *
     * @see ApiResponse
     * @see Guest
     * @see User
     */
    ApiResponse<List<AccountResponse>> buildAccountResponse(Guest guest, User user);
}

