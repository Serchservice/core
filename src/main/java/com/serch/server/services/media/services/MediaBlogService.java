package com.serch.server.services.media.services;


import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaBlogResponse;

import java.util.List;

/**
 * Service interface for managing media blog posts.
 *
 * @see MediaBlogImplementation
 */
public interface MediaBlogService {

    /**
     * Retrieves a specific media blog post by its key.
     *
     * @param key The unique key of the media blog post to retrieve.
     * @return ApiResponse containing the MediaBlogResponse if found.
     *
     * @see MediaBlogResponse
     * @see ApiResponse
     */
    ApiResponse<MediaBlogResponse> findBlog(String key);

    /**
     * Retrieves all media blog posts.
     *
     * @param page The page number for pagination (optional).
     * @return ApiResponse containing a list of MediaBlogResponse objects.
     *
     * @see MediaBlogResponse
     * @see ApiResponse
     */
    ApiResponse<List<MediaBlogResponse>> findAllBlogs(Integer page);
}