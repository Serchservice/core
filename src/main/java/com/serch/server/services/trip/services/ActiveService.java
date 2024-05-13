package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.trip.TripStatus;
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
    ApiResponse<TripStatus> toggleStatus(OnlineRequest request);

    /**
     * Fetches the current trip status.
     *
     * @return An ApiResponse containing the current trip status.
     */
    ApiResponse<TripStatus> fetchStatus();

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
     * @see TripStatus
     * @see OnlineRequest
     */
    void toggle(User user, TripStatus status, OnlineRequest request);

    /**
     * Searches for active providers based on the provided query and location parameters.
     *
     * @param query     The search query.
     * @param category  The category of the providers to search for.
     * @param longitude The longitude coordinate.
     * @param latitude  The latitude coordinate.
     * @param radius    The search radius.
     * @return An ApiResponse containing a list of ActiveResponse objects.
     *
     * @see ApiResponse
     * @see ActiveResponse
     */
    ApiResponse<List<ActiveResponse>> search(
            String query, String category, Double longitude, Double latitude, Double radius
    );

    /**
     * Automatically finds the best match provider based on the provided query and location parameters.
     *
     * @param query     The search query.
     * @param category  The category of the providers to search for.
     * @param longitude The longitude coordinate.
     * @param latitude  The latitude coordinate.
     * @param radius    The search radius.
     * @return An ApiResponse containing the best match ActiveResponse object.
     *
     * @see ApiResponse
     * @see ActiveResponse
     */
    ApiResponse<ActiveResponse> auto(
            String query, String category, Double longitude, Double latitude, Double radius
    );
}