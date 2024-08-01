package com.serch.server.core.map.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.map.responses.Address;

import java.util.List;

/**
 * Interface for location-related services.
 *
 * @see LocationImplementation
 */
public interface LocationService {
    /**
     * Retrieves predictions for a given query.
     *
     * @param query The query to search for predictions.
     * @return An {@link ApiResponse} containing a list of {@link Address} objects.
     */
    ApiResponse<List<Address>> predictions(String query);

    /**
     * Searches for a place using its ID.
     *
     * @param id The ID of the place to search for.
     * @return An {@link ApiResponse} containing a {@link Address} object representing the found place.
     */
    ApiResponse<Address> search(String id);
}