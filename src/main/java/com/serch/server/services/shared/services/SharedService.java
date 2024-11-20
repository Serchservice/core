package com.serch.server.services.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedLogin;
import com.serch.server.models.shared.SharedStatus;
import com.serch.server.models.trip.Trip;
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
     *
     * @return {@link ApiResponse} list of {@link AccountResponse}
     */
    ApiResponse<List<AccountResponse>> accounts(String id);

    /**
     * Create provide sharing link
     *
     * @param withInvited Whether to create the data from the invited provider
     * @param id The trip id to create a {@link SharedLink} data from
     *
     * @return {@link ApiResponse} list of {@link SharedLinkResponse}
     */
    ApiResponse<List<SharedLinkResponse>> create(String id, Boolean withInvited);

    /**
     * Retrieves a list of guest responses.
     *
     * @return {@link ApiResponse} list of {@link SharedLinkResponse}
     */
    ApiResponse<List<SharedLinkResponse>> links();

    /**
     * Build the response of the shared link
     *
     * @param link The {@link SharedLink} data
     *
     * @return Response of {@link SharedLinkResponse}
     */
    SharedLinkResponse buildLink(SharedLink link);

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
     * Create a shared status data for the trip data
     *
     * @param linkId The {@link SharedLink} id
     * @param account The {@link Guest} id
     * @param trip The {@link Trip} data
     */
    void create(String linkId, String account, Trip trip);

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

