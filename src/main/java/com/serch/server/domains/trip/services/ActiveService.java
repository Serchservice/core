package com.serch.server.domains.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.models.auth.User;
import com.serch.server.domains.trip.requests.OnlineRequest;
import com.serch.server.domains.trip.responses.ActiveResponse;
import com.serch.server.domains.trip.responses.MapViewResponse;

import java.util.List;

/**
 * Service interface for managing the active status of users.
 * <p>
 * This interface defines methods for managing the active or online status of users,
 * fetching the list of active providers, and retrieving user locations on the location.
 * </p>
 *
 * @see com.serch.server.domains.trip.services.implementations.ActiveImplementation
 */
public interface ActiveService {

    /**
     * Toggles the trip status based on the provided request.
     * <p>
     * This method updates the trip status of the user, setting it as active or inactive
     * based on the information in the provided {@link OnlineRequest}.
     * </p>
     *
     * @param request The {@link OnlineRequest} containing details for updating the trip status.
     * @return An {@link ApiResponse} containing the updated {@link ProviderStatus}.
     */
    ApiResponse<ProviderStatus> toggleStatus(OnlineRequest request);

    /**
     * Fetches the current trip status.
     * <p>
     * Retrieves the current trip status of the user, indicating whether the user
     * is currently active, inactive, or in another state.
     * </p>
     *
     * @return An {@link ApiResponse} containing the current {@link ProviderStatus}.
     */
    ApiResponse<ProviderStatus> fetchStatus();

    /**
     * Fetches the list of active providers for a business.
     * <p>
     * This method returns a list of active providers who are currently available
     * for service, filtered according to the business's criteria.
     * </p>
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return An {@link ApiResponse} containing the list of {@link ActiveResponse} representing active providers.
     */
    ApiResponse<List<ActiveResponse>> activeList(Integer page, Integer size);

    /**
     * Toggles the trip status of the specified user.
     * <p>
     * This method updates the status of a specific user to the provided {@link ProviderStatus},
     * and performs additional updates based on the details in the given {@link OnlineRequest}.
     * </p>
     *
     * @param user    The {@link User} whose trip status will be toggled.
     * @param status  The new {@link ProviderStatus} to be applied (e.g., active, inactive).
     * @param request The {@link OnlineRequest} containing additional details for the update.
     *
     * @see User
     * @see ProviderStatus
     * @see OnlineRequest
     */
    void toggle(User user, ProviderStatus status, OnlineRequest request);

    /**
     * Retrieves the location view location of the user.
     * <p>
     * This method provides the location response of the specified user for location visualization.
     * It returns a {@link MapViewResponse} that contains the user's geographic coordinates
     * and other location-related information.
     * </p>
     *
     * @param user The {@link User} whose location is to be retrieved.
     * @return A {@link MapViewResponse} containing the user's location response for the location view.
     *
     * @see User
     * @see ProviderStatus
     * @see OnlineRequest
     */
    MapViewResponse getLocation(User user);
}