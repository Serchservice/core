package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.Gender;
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
     * This sets the gender preference for trip service for the user
     *
     * @param gender The preferred gender for trips
     *
     * @return ApiResponse of String
     *
     * @see ApiResponse
     */
    ApiResponse<String> setGenderForTrip(Gender gender);

    /**
     * This fetches the account settings for the user
     *
     * @return ApiResponse of {@link AccountSettingResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<AccountSettingResponse> settings();
}