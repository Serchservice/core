package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaLegalGroupResponse;
import com.serch.server.services.media.responses.MediaLegalResponse;

import java.util.List;

/**
 * Service interface for managing media legal documents.
 *
 * @see MediaLegalImplementation
 */
public interface MediaLegalService {

    /**
     * Retrieves all media legal documents grouped by line of business.
     *
     * @return ApiResponse containing a list of MediaLegalGroupResponse objects.
     *
     * @see ApiResponse
     * @see MediaLegalGroupResponse
     */
    ApiResponse<List<MediaLegalGroupResponse>> fetchAllLegals();

    /**
     * Retrieves a specific media legal document by its key.
     *
     * @param key The unique key of the media legal document to retrieve.
     * @return ApiResponse containing the MediaLegalResponse if found.
     *
     * @see MediaLegalResponse
     * @see ApiResponse
     */
    ApiResponse<MediaLegalResponse> fetchLegal(String key);
}