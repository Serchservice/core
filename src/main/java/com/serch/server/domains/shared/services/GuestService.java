package com.serch.server.domains.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.shared.SharedLogin;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.shared.requests.GuestToUserRequest;
import com.serch.server.domains.shared.requests.SwitchRequest;
import com.serch.server.domains.shared.responses.GuestResponse;

/**
 * Service interface for guest-related operations.
 *
 * @see com.serch.server.domains.shared.services.implementations.GuestImplementation
 */
public interface GuestService {
    /**
     * Converts a guest to a user.
     *
     * @param request The request containing the information to convert a guest to a user.
     * @return A response containing authentication information for the newly created user.
     *
     * @see GuestToUserRequest
     */
    ApiResponse<AuthResponse> becomeAUser(GuestToUserRequest request);

    /**
     * Refresh guest data intermittently
     *
     * @param request The {@link SwitchRequest} data containing core values needed to update guest data
     */
    void refresh(SwitchRequest request);

    /**
     * Prepares the guest data
     *
     * @param login The SharedLink for data preparation {@link SharedLogin}
     *
     * @return {@link GuestResponse}
     */
    GuestResponse response(SharedLogin login);

    /**
     * Update the FCM token of the logged-in user
     *
     * @param token The new fcm token
     * @param guest The guest id
     *
     * @return {@link ApiResponse} of Success or failure
     */
    ApiResponse<String> updateFcmToken(String token, String guest);

    /**
     * Update the timezone of the logged-in user
     *
     * @param timezone The new timezone
     *
     * @return {@link ApiResponse} of Success or failure
     */
    ApiResponse<String> updateTimezone(String timezone, String guest);
}