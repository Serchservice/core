package com.serch.server.domains.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.shared.requests.GuestToUserRequest;
import com.serch.server.domains.shared.requests.SwitchRequest;

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
     * Refresh guest response intermittently
     *
     * @param request The {@link SwitchRequest} response containing services values needed to update guest response
     */
    void refresh(SwitchRequest request);

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
}