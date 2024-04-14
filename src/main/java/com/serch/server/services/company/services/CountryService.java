package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.CountryRequest;

/**
 * Service interface for managing country-related operations.
 *
 * @see com.serch.server.services.company.services.implementations.CountryImplementation
 */
public interface CountryService {

    /**
     * Checks if the provided location is launched in the Serch platform.
     *
     * @param request The CountryRequest object containing location details.
     * @return ApiResponse containing a message about the location's status.
     *
     * @see CountryRequest
     * @see ApiResponse
     */
    ApiResponse<String> checkMyLocation(CountryRequest request);

    /**
     * Requests a new location to be added to the Serch platform.
     *
     * @param request The CountryRequest object containing location details.
     * @return ApiResponse indicating the success or failure of the request.
     *
     * @see CountryRequest
     * @see ApiResponse
     */
    ApiResponse<String> requestMyLocation(CountryRequest request);
}

