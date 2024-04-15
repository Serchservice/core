package com.serch.server.services.map.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.map.responses.Place;
import com.serch.server.services.map.responses.Prediction;

import java.util.List;

/**
 * Interface for location-related services.
 *
 * @see LocationImplementation
 */
public interface LocationService {
    /**
     * Retrieves predictions for a given address.
     *
     * @param address The address to search for predictions.
     * @return An {@link ApiResponse} containing a list of {@link Prediction} objects.
     */
    ApiResponse<List<Prediction>> predictions(String address);

    /**
     * Searches for a place using its ID.
     *
     * @param id The ID of the place to search for.
     * @return An {@link ApiResponse} containing a {@link Place} object representing the found place.
     */
    ApiResponse<Place> search(String id);
}