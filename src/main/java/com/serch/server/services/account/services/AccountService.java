package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.account.responses.DashboardResponse;

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

    /**
     * Fetches the dashboard details for the logged-in user
     *
     * @return {@link ApiResponse} of {@link DashboardResponse}
     */
    ApiResponse<DashboardResponse> dashboard();

    /**
     * Fetches the dashboard response of all business associates
     *
     * @return {@link ApiResponse} of list {@link DashboardResponse}
     */
    ApiResponse<List<DashboardResponse>> dashboards();
}
