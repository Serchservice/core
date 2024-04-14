package com.serch.server.services.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.shared.requests.SwitchRequest;
import com.serch.server.services.shared.responses.GuestResponse;

/**
 * Service interface for switching between guest and user accounts.
 *
 * @see com.serch.server.services.shared.services.implementations.SwitchImplementation
 */
public interface SwitchService {
    /**
     * Switches to a guest account based on the provided request.
     *
     * @param request The switch request containing necessary information.
     * @return A response containing the guest information.
     *
     * @see SwitchRequest
     */
    ApiResponse<GuestResponse> switchToGuest(SwitchRequest request);

    /**
     * Switches to a user account based on the provided request.
     *
     * @param request The switch request containing necessary information.
     * @return A response containing the user authentication information.
     *
     * @see SwitchRequest
     */
    ApiResponse<AuthResponse> switchToUser(SwitchRequest request);
}

