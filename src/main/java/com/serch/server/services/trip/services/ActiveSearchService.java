package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.models.account.Profile;
import com.serch.server.services.trip.responses.ActiveResponse;
import com.serch.server.services.trip.responses.SearchResponse;

/**
 * Service interface for searching active providers.
 *
 * @see com.serch.server.services.trip.services.implementations.ActiveSearchImplementation
 */
public interface ActiveSearchService {

    /**
     * Retrieves the search radius to be used for the search
     *
     * @param radius Nullable radius
     *
     * @return The search radius to apply
     */
    Double getSearchRadius(Double radius);

    /**
     * Generates an {@link ActiveResponse} based on the profile, status, and distance.
     *
     * @param profile The profile of the active provider.
     * @param status  The trip status of the active provider.
     * @param distance The distance from the user's location to the active provider.
     * @return An {@link ActiveResponse} representing the active provider's response.
     *
     * @see Profile
     * @see ProviderStatus
     */
    ActiveResponse response(Profile profile, ProviderStatus status, double distance);

    /**
     * Searches for active providers by category within a specified radius.
     *
     * @param category The search category.
     * @param longitude The longitude of the user's location.
     * @param latitude The latitude of the user's location.
     * @param radius The search radius in kilometers.
     * @param autoConnect Checks if the request has the find best match request
     *
     * @return An {@link ApiResponse} containing {@link SearchResponse} object for providers
     *
     * @see SerchCategory
     */
    ApiResponse<SearchResponse> search(SerchCategory category, Double longitude, Double latitude, Double radius, Boolean autoConnect);

    /**
     * Searches for active providers by specialty within a specified radius.
     *
     * @param query The search query for specialty.
     * @param longitude The longitude of the user's location.
     * @param latitude The latitude of the user's location.
     * @param radius The search radius in kilometers.
     * @param autoConnect Checks if the request has the find best match request
     *
     * @return An {@link ApiResponse} containing {@link SearchResponse} object for providers and shops
     */
    ApiResponse<SearchResponse> search(String query, Double longitude, Double latitude, Double radius, Boolean autoConnect);
}