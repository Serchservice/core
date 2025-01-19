package com.serch.server.domains.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.account.requests.UpdateE2EKey;
import com.serch.server.domains.account.responses.AccountResponse;

import java.util.List;

/**
 * This is the wrapper class that performs any action related to a logged-in user account
 *
 * @see com.serch.server.domains.account.services.implementations.AccountImplementation
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
    ApiResponse<List<AccountResponse>> accounts();

    /**
     * Gets the last updated time for user password
     *
     * @return ApiResponse of String
     */
    ApiResponse<String> lastPasswordUpdateAt();

    /**
     * Update the FCM token of the logged-in user
     *
     * @param token The new fcm token
     *
     * @return {@link ApiResponse} of Success or failure
     */
    ApiResponse<String> updateFcmToken(String token);

    /**
     * Update the timezone of the logged-in user
     *
     * @param timezone The new timezone
     *
     * @return {@link ApiResponse} of Success or failure
     */
    ApiResponse<String> updateTimezone(String timezone);

    /**
     * Update the public encryption key of the logged-in user
     *
     * @param key The new key response {@link UpdateE2EKey}
     *
     * @return {@link ApiResponse} of Success or failure
     */
    ApiResponse<String> updatePublicEncryptionKey(UpdateE2EKey key);
}
