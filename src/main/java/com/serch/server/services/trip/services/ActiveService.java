package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.models.auth.User;
import com.serch.server.services.trip.requests.OnlineRequest;
import com.serch.server.services.trip.responses.ActiveResponse;

import java.util.List;

/**
 * Service interface for managing the active status of users.
 * <p></p>
 * @see com.serch.server.services.trip.services.implementations.ActiveImplementation
 */
public interface ActiveService {
    /**
     * Toggles the trip status based on the provided request.
     *
     * @param request The request containing details to update the trip status.
     * @return An ApiResponse containing the updated trip status.
     */
    ApiResponse<ProviderStatus> toggleStatus(OnlineRequest request);

    /**
     * Fetches the current trip status.
     *
     * @return An ApiResponse containing the current trip status.
     */
    ApiResponse<ProviderStatus> fetchStatus();

    /**
     * Fetches the list of active providers for a business
     *
     * @return An ApiResponse containing the list of {@link ActiveResponse}
     */
    ApiResponse<List<ActiveResponse>> activeList();

    /**
     * Toggles the trip status of the specified user.
     *
     * @param user    The user whose trip status will be toggled.
     * @param status  The new trip status.
     * @param request The request containing details to update the trip status.
     *
     * @see User
     * @see ProviderStatus
     * @see OnlineRequest
     */
    void toggle(User user, ProviderStatus status, OnlineRequest request);
}