package com.serch.server.domains.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedLogin;
import com.serch.server.models.shared.SharedStatus;
import com.serch.server.models.trip.Trip;
import com.serch.server.domains.account.responses.AccountResponse;
import com.serch.server.domains.shared.responses.SharedLinkData;
import com.serch.server.domains.shared.responses.SharedLinkResponse;
import com.serch.server.domains.shared.responses.SharedStatusData;

import java.util.List;

/**
 * Service interface for shared operations.
 *
 * @see com.serch.server.domains.shared.services.implementations.SharedImplementation
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
     * @param withInvited Whether to create the response from the invited provider
     * @param id The trip id to create a {@link SharedLink} response from
     *
     * @return {@link ApiResponse} list of {@link SharedLinkResponse}
     */
    ApiResponse<List<SharedLinkResponse>> create(String id, Boolean withInvited);

    /**
     * Retrieves a list of guest responses.
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return {@link ApiResponse} list of {@link SharedLinkResponse}
     */
    ApiResponse<List<SharedLinkResponse>> links(Integer page, Integer size);

    /**
     * Build the response of the shared link
     *
     * @param link The {@link SharedLink} response
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
     * Prepares SharedLink response response
     *
     * @param link The SharedLink response to prepare response with
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
     * @param link The SharedLink response
     * @param status The SharedStatus response
     *
     * @return SharedPricingData response {@link SharedStatusData}
     */
    SharedStatusData getStatusData(SharedLink link, SharedStatus status);

    /**
     * Create a shared status response for the trip response
     *
     * @param linkId The {@link SharedLink} id
     * @param account The {@link Guest} id
     * @param trip The {@link Trip} response
     */
    void create(String linkId, String account, Trip trip);

    /**
     * Builds an account response object for the given user.
     * The response type is specified by the generic type parameter.
     *
     * @param user     The user for whom the account response is being built.
     * @param response The instance of the account response to populate.
     * @param <T>      The type of the account response, extending {@link AccountResponse}.
     * @return The populated account response of type T.
     */
    <T extends AccountResponse> T buildWithUser(User user, T response);

    /**
     * Builds an account response object for the given user.
     * The response type is specified by the generic type parameter.
     *
     * @param user     The user for whom the account response is being built.
     * @param response The instance of the account response to populate.
     * @param <T>      The type of the account response, extending {@link AccountResponse}.
     * @return The populated account response of type T.
     */
    <T extends AccountResponse> T buildWithLogin(SharedLogin user, T response);

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

