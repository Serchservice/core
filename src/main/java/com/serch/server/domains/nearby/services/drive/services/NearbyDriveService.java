package com.serch.server.domains.nearby.services.drive.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.drive.requests.NearbyDriveRequest;

/**
 * Service interface for managing nearby search and drive operations.
 */
public interface NearbyDriveService {

    /**
     * Searches for items of the specified type.
     *
     * @param type the type of item to search for.
     * @return an {@link ApiResponse} containing the search results as a {@link String}.
     */
    ApiResponse<String> search(String type);

    /**
     * Executes a drive operation based on the provided request.
     *
     * @param request the request containing details for the drive operation.
     * @return an {@link ApiResponse} containing the result of the drive operation as a {@link String}.
     */
    ApiResponse<String> drive(NearbyDriveRequest request);
}