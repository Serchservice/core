package com.serch.server.services.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.shared.requests.GuestToUserRequest;
import com.serch.server.services.shared.responses.GuestActivityResponse;

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
     * Checks if the email associated with a guest is confirmed.
     *
     * @param guestId The ID of the guest.
     * @return A response indicating whether the email is confirmed or not.
     */
    ApiResponse<String> checkIfEmailIsConfirmed(String guestId);

    /**
     * Converts a guest to a user.
     *
     * @param request The request containing the information to convert a guest to a user.
     * @return A response containing authentication information for the newly created user.
     *
     * @see GuestToUserRequest
     */
    ApiResponse<AuthResponse> becomeAUser(GuestToUserRequest request);

    // TODO: Add connection method here with WebSocket
}
