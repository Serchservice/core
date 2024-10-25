package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.models.account.Profile;
import com.serch.server.services.trip.responses.ActiveResponse;
import com.serch.server.services.trip.responses.SearchResponse;

/**
 * Service interface for searching active providers.
 * <p>
 * This interface defines methods for searching active providers within a specified
 * radius, generating responses for active providers, and retrieving the search radius.
 * </p>
 *
 * @see com.serch.server.services.trip.services.implementations.ActiveSearchImplementation
 */
public interface ActiveSearchService {

    /**
     * Retrieves the effective search radius to be used for finding active providers.
     * <p>
     * This method determines the radius value to be applied when searching for
     * providers. If the provided radius is null, a default value may be applied.
     * </p>
     *
     * @param radius Nullable search radius in kilometers. If null, a default radius will be used.
     * @return The effective search radius in kilometers.
     */
    Double getSearchRadius(Double radius);

    /**
     * Generates an {@link ActiveResponse} based on the provider's profile, status, and distance.
     * <p>
     * This method creates an {@link ActiveResponse} object that contains details about
     * an active provider, including their profile information, current status, and the
     * distance from the user.
     * </p>
     *
     * @param profile The profile of the active provider containing account and contact details.
     * @param status The current status of the active provider, such as available or busy.
     * @param distance The distance in kilometers from the user's location to the active provider.
     * @return An {@link ActiveResponse} representing the active provider's details.
     *
     * @see Profile
     * @see ProviderStatus
     */
    ActiveResponse response(Profile profile, ProviderStatus status, double distance);

    /**
     * Searches for active providers by category within a specified radius from the user's location.
     * <p>
     * This method performs a search for active providers who belong to a specified category
     * and are located within the given radius from the user's current location. The search
     * results may include options to auto-connect with the best match.
     * </p>
     *
     * @param category The search category used to filter providers (e.g., healthcare, automotive).
     * @param longitude The longitude coordinate of the user's location.
     * @param latitude The latitude coordinate of the user's location.
     * @param radius The search radius in kilometers within which to find providers.
     * @param autoConnect A flag indicating whether to find the best matching provider automatically.
     * @return An {@link ApiResponse} containing a {@link SearchResponse} with the list of matching providers.
     *
     * @see SerchCategory
     */
    ApiResponse<SearchResponse> search(SerchCategory category, Double longitude, Double latitude, Double radius, Boolean autoConnect);

    /**
     * Searches for active providers by specialty within a specified radius from the user's location.
     * <p>
     * This method allows searching for providers based on a specialty keyword, such as a specific
     * service or skill. The search is conducted within the specified radius from the user's
     * location, and results can include both individual providers and shops.
     * </p>
     *
     * @param query The search query representing the specialty or skill being searched for.
     * @param longitude The longitude coordinate of the user's location.
     * @param latitude The latitude coordinate of the user's location.
     * @param radius The search radius in kilometers within which to find providers or shops.
     * @param autoConnect A flag indicating whether to automatically find the best match.
     * @return An {@link ApiResponse} containing a {@link SearchResponse} with the list of matching providers and shops.
     */
    ApiResponse<SearchResponse> search(String query, Double longitude, Double latitude, Double radius, Boolean autoConnect);
}