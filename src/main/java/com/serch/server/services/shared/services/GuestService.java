package com.serch.server.services.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLogin;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.shared.requests.GuestToUserRequest;
import com.serch.server.services.shared.responses.GuestActivityResponse;
import com.serch.server.services.shared.responses.GuestResponse;

/**
 * Service interface for guest-related operations.
 *
 * @see com.serch.server.services.shared.services.implementations.GuestImplementation
 */
public interface GuestService {
    /**
     * Retrieves guest activity information.
     *
     * @param guestId The ID of the guest.
     * @param linkId  The ID of the link associated with the guest.
     * @return A response containing the guest activity information.
     */
    ApiResponse<GuestActivityResponse> activity(String guestId, String linkId);

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
     * This updates and checks if a shared link can be used again.
     * If yes, it will change the status
     * of the shared link accordingly
     *
     * @param login The SharedLogin details
     */
    void checkLink(SharedLogin login);
    // TODO: Add connection method here with WebSocket
}