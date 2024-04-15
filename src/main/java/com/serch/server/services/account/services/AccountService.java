package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.shared.requests.SwitchRequest;
import com.serch.server.services.shared.responses.GuestResponse;

/**
 * This is the wrapper class that performs any action related to a logged-in user account
 *
 * @see com.serch.server.services.account.services.implementations.AccountImplementation
 */
public interface AccountService {
    /**
     * Retrieves account information based on the logged-in user id.
     *
     * @return A response containing the account information.
     *
     * @see ApiResponse
     * @see AccountResponse
     */
    ApiResponse<AccountResponse> accounts();

    /**
     * Switches to a guest account based on the provided request.
     *
     * @param request The switch request containing necessary information.
     * @return A response containing the guest information.
     *
     * @see SwitchRequest
     * @see ApiResponse
     * @see GuestResponse
     */
    ApiResponse<GuestResponse> switchToGuest(SwitchRequest request);

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
    ApiResponse<AccountResponse> buildAccountResponse(Guest guest, User user);
}
