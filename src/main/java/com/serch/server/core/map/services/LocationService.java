package com.serch.server.core.map.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.map.responses.Address;

import java.util.List;

/**
 * Interface for location-related services.
 * <p>
 * This interface provides methods for retrieving location predictions and
 * searching for places based on their unique identifiers. Implementations
 * of this interface should interact with the underlying mapping or location
 * services to provide accurate and timely results for user queries.
 * </p>
 *
 * @see LocationImplementation
 */
public interface LocationService {

    /**
     * Retrieves predictions for a given query.
     * <p>
     * This method accepts a user-provided query string and returns a list
     * of potential address matches. It is commonly used for auto-complete
     * functionality, allowing users to receive suggestions as they type.
     * The results are encapsulated within an {@link ApiResponse}, which
     * can also include metadata about the request status.
     * </p>
     *
     * @param query The query to search for predictions. This is typically a
     *              partial address or location name that the user is typing.
     * @return An {@link ApiResponse} containing a list of {@link Address}
     *         objects that match the query. If no predictions are found,
     *         the list may be empty, and the response should indicate the
     *         status of the request.
     */
    ApiResponse<List<Address>> predictions(String query);

    /**
     * Searches for a place using its ID.
     * <p>
     * This method takes a unique identifier for a location and returns
     * the corresponding address details. This is useful for retrieving
     * full information about a specific place that has already been
     * identified, such as when a user selects a location from a list
     * of predictions.
     * </p>
     *
     * @param id The ID of the place to search for. This should correspond
     *           to an existing location in the mapping service's database.
     * @return An {@link ApiResponse} containing a {@link Address} object
     *         representing the found place. If the ID does not correspond
     *         to a valid location, the response should indicate this
     *         status appropriately.
     */
    ApiResponse<Address> search(String id);
}