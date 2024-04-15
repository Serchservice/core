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
     * @return ApiResponse containing a list of SpecialtyResponse objects.
     *
     * @see ApiResponse
     * @see SpecialtyKeywordResponse
     */
    ApiResponse<List<SpecialtyKeywordResponse>> getAllSpecialties(SerchCategory type);

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
}