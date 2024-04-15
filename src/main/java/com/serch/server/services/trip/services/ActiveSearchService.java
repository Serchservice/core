package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.models.account.Profile;
import com.serch.server.services.trip.responses.ActiveResponse;

import java.util.List;

/**
 * Service interface for searching active providers.
 *
 * @see com.serch.server.services.trip.services.implementations.ActiveSearchImplementation
 */
public interface ActiveSearchService {

    /**
     * Generates an {@link ActiveResponse} based on the profile, status, and distance.
     *
     * @param profile The profile of the active provider.
     * @param status  The trip status of the active provider.
     * @param distance The distance from the user's location to the active provider.
     * @return An {@link ActiveResponse} representing the active provider's response.
     *
     * @see Profile
     * @see TripStatus
     */
    ActiveResponse response(Profile profile, TripStatus status, double distance);

    /**
     * Searches for active providers by category within a specified radius.
     *
     * @param category The search category.
     * @param longitude The longitude of the user's location.
     * @param latitude The latitude of the user's location.
     * @param radius The search radius in kilometers.
     * @return An {@link ApiResponse} containing a list of {@link ActiveResponse} objects
     *         filtered by the specified category.
     *
     * @see SerchCategory
     */
    ApiResponse<List<ActiveResponse>> searchByCategory(SerchCategory category, Double longitude, Double latitude, Double radius);

    /**
     * Searches for active providers by verification status within a specified radius.
     *
     * @param category The search category.
     * @param longitude The longitude of the user's location.
     * @param latitude The latitude of the user's location.
     * @param radius The search radius in kilometers.
     * @return An {@link ApiResponse} containing a list of {@link ActiveResponse} objects
     *         filtered by verification status.
     *
     * @see SerchCategory
     */
    ApiResponse<List<ActiveResponse>> searchByVerified(SerchCategory category, Double longitude, Double latitude, Double radius);

    /**
     * Searches for active providers offering free services within a specified radius.
     *
     * @param category The search category.
     * @param longitude The longitude of the user's location.
     * @param latitude The latitude of the user's location.
     * @param radius The search radius in kilometers.
     * @return An {@link ApiResponse} containing a list of {@link ActiveResponse} objects
     *         filtered by free status.
     *
     * @see SerchCategory
     */
    ApiResponse<List<ActiveResponse>> searchByFree(SerchCategory category, Double longitude, Double latitude, Double radius);

    /**
     * Searches for active providers sorted by rating within a specified radius.
     *
     * @param category The search category.
     * @param longitude The longitude of the user's location.
     * @param latitude The latitude of the user's location.
     * @param radius The search radius in kilometers.
     * @return An {@link ApiResponse} containing a list of {@link ActiveResponse} objects
     *         sorted by rating.
     *
     * @see SerchCategory
     */
    ApiResponse<List<ActiveResponse>> searchByRating(SerchCategory category, Double longitude, Double latitude, Double radius);

    /**
     * Searches for active providers by specialty within a specified radius.
     *
     * @param query The search query for specialty.
     * @param longitude The longitude of the user's location.
     * @param latitude The latitude of the user's location.
     * @param radius The search radius in kilometers.
     * @return An {@link ApiResponse} containing a list of {@link ActiveResponse} objects
     *         filtered by specialty.
     */
    ApiResponse<List<ActiveResponse>> searchBySpecialty(String query, Double longitude, Double latitude, Double radius);
}