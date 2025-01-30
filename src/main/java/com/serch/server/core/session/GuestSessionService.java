package com.serch.server.core.session;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.shared.responses.GuestResponse;
import com.serch.server.models.shared.SharedLogin;

/**
 * Interface defining methods for managing guest sessions.
 */
public interface GuestSessionService {

    /**
     * Processes a guest login request and returns a response containing relevant information.
     *
     * @param login The login credentials containing details required for guest login.
     * @return A {@link GuestResponse} object containing the guest session details or an error message.
     */
    GuestResponse response(SharedLogin login);

    /**
     * Validates a guest session token and returns an API response indicating success or failure.
     *
     * @param token The guest session token to be validated.
     * @return An {@link ApiResponse} object with a success flag and an optional message.
     */
    ApiResponse<String> validateSession(String token);
}