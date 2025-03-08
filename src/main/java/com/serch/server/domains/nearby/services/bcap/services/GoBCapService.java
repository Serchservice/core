package com.serch.server.domains.nearby.services.bcap.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.bcap.requests.GoBCapCreateRequest;
import com.serch.server.domains.nearby.services.bcap.responses.GoBCapResponse;

import java.util.List;

/**
 * This interface defines the contract for managing GoBCap entities.
 * <p></p>
 * It provides methods for creating, retrieving, and deleting GoBCap objects.
 */
public interface GoBCapService {
    /**
     * Creates a new GoBCap.
     *
     * @param request A {@link GoBCapCreateRequest} containing details of the associated media files.
     * @return An {@link ApiResponse} containing the created GoBCapResponse object.
     */
    ApiResponse<GoBCapResponse> create(GoBCapCreateRequest request);

    /**
     * Retrieves a specific GoBCap by its ID.
     *
     * @param id The ID of the GoBCap to retrieve.
     * @return An {@link ApiResponse} containing the GoBCapResponse object.
     */
    ApiResponse<GoBCapResponse> get(String id);

    /**
     * Retrieves a list of GoBCap objects based on the specified criteria.
     *
     * @param lng The longitude of the user's address.
     * @param lat The latitude of the user's address.
     * @param page The page number for pagination.
     * @param size The number of bcaps per page.
     * @param interest The ID of the interest associated with the bcaps.
     * @param scoped A flag indicating whether to filter events created by the current user.
     *
     * @return An {@link ApiResponse} containing a list of GoBCapResponse objects.
     */
    ApiResponse<List<GoBCapResponse>> getAll(Integer page, Integer size, Long interest, Boolean scoped, Double lat, Double lng);

    /**
     * Deletes a bcap.
     *
     * @param id The ID of the bcap to delete.
     * @return An {@link ApiResponse} containing a success message or an error message.
     */
    ApiResponse<Void> delete(String id);
}