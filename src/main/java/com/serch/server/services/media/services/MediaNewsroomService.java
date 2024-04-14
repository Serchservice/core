package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaNewsroomResponse;

import java.util.List;

/**
 * Service interface for managing media news articles.
 *
 * @see MediaNewsroomImplementation
 */
public interface MediaNewsroomService {

    /**
     * Retrieves a specific media news article by its key.
     *
     * @param key The unique key of the media news article to retrieve.
     * @return ApiResponse containing the MediaNewsroomResponse if found.
     *
     * @see MediaNewsroomResponse
     * @see ApiResponse
     */
    ApiResponse<MediaNewsroomResponse> findNews(String key);

    /**
     * Retrieves all media news articles with pagination support.
     *
     * @param page The page number for pagination (optional, defaults to 1).
     * @return ApiResponse containing a list of MediaNewsroomResponse objects.
     *
     * @see MediaNewsroomResponse
     * @see ApiResponse
     */
    ApiResponse<List<MediaNewsroomResponse>> findAllNews(Integer page);
}