package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.company.SpecialtyKeyword;
import com.serch.server.services.company.responses.SpecialtyKeywordResponse;
import com.serch.server.services.company.services.implementations.SpecialtyKeywordImplementation;

import java.util.List;

/**
 * Service interface for managing keywords.
 *
 * @see SpecialtyKeywordImplementation
 */
public interface SpecialtyKeywordService {

    /**
     * Retrieves all specialties based on the specified category.
     *
     * @param type The category of specialties to retrieve.
     * @return List of SpecialtyResponse objects.
     *
     * @see SpecialtyKeywordResponse
     */
    List<SpecialtyKeywordResponse> getAllSpecialties(SerchCategory type);

    /**
     * Maps a SpecialtyService object to a SpecialtyResponse object.
     *
     * @param keywordService The SpecialtyService object to map.
     * @return The mapped SpecialtyResponse object.
     *
     * @see SpecialtyKeyword
     * @see SpecialtyKeywordResponse
     */
    SpecialtyKeywordResponse getSpecialtyResponse(SpecialtyKeyword keywordService);

    /**
     * Searches for specialty keywords based on a query.
     *
     * @param query The search query to find specialty keywords.
     * @return An {@link ApiResponse} containing a list of {@link SpecialtyKeywordResponse} objects
     *         filtered by the query.
     */
    ApiResponse<List<SpecialtyKeywordResponse>> searchService(String query);
}