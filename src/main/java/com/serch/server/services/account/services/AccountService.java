package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.AccountResponse;

import java.util.List;

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
    ApiResponse<List<AccountResponse>> accounts();

    /**
     * Gets the last updated time for user password
     *
     * @return ApiResponse of String
     */
    ApiResponse<String> lastPasswordUpdateAt();
}
