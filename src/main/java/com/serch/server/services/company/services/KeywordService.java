package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.company.SpecialtyService;
import com.serch.server.services.auth.responses.SpecialtyResponse;

import java.util.List;

/**
 * Service interface for managing keywords.
 *
 * @see com.serch.server.services.company.services.implementations.ServiceKeywordImplementation
 */
public interface KeywordService {

    /**
     * Retrieves all specialties based on the specified category.
     *
     * @param type The category of specialties to retrieve.
     * @return ApiResponse containing a list of SpecialtyResponse objects.
     *
     * @see ApiResponse
     * @see SpecialtyResponse
     */
    ApiResponse<List<SpecialtyResponse>> getAllSpecialties(SerchCategory type);

    /**
     * Maps a SpecialtyService object to a SpecialtyResponse object.
     *
     * @param keywordService The SpecialtyService object to map.
     * @return The mapped SpecialtyResponse object.
     *
     * @see SpecialtyService
     * @see SpecialtyResponse
     */
    SpecialtyResponse getSpecialtyResponse(SpecialtyService keywordService);
}