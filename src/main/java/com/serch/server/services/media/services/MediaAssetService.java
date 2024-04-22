package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaAssetResponse;

import java.util.List;

/**
 * Service interface for managing media assets.
 *
 * @see MediaAssetImplementation
 */
public interface MediaAssetService {

    /**
     * Retrieves all media assets.
     *
     * @return ApiResponse containing a list of MediaAssetResponse objects.
     *
     * @see MediaAssetResponse
     */
    ApiResponse<List<MediaAssetResponse>> fetchAllAssets();

    /**
     * THis increments the download count.
     *
     * @param key The asset to be downloaded
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     */
    ApiResponse<String> download(Long key);
}