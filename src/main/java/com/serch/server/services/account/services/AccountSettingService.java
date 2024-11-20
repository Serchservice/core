package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.account.responses.AccountSettingResponse;

/**
 * This is the wrapper class for the implementation of account settings
 *
 * @see com.serch.server.services.account.services.implementations.AccountSettingImplementation
 */
public interface AccountSettingService {
    /**
     * Create the default setting for the user
     *
     * @param user The user whose setting is being created
     */
    void create(User user);

    /**
     * This updates preference for trip service for the user
     *
     * @param request The {@link AccountSettingResponse} update data
     *
     * @return ApiResponse of {@link AccountSettingResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<AccountSettingResponse> update(AccountSettingResponse request);

    /**
     * This fetches the account settings for the user
     *
     * @return ApiResponse of {@link AccountSettingResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<AccountSettingResponse> settings();
}