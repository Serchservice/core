package com.serch.server.services.bookmark.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.bookmark.AddBookmarkRequest;
import com.serch.server.services.bookmark.BookmarkResponse;

import java.util.List;

/**
 * Service interface for managing bookmarks.
 */
public interface BookmarkService {
    /**
     * Adds a bookmark for a provider.
     *
     * @param request The AddBookmarkRequest containing the user and provider information.
     * @return ApiResponse indicating the success of the operation.
     *
     * @see AddBookmarkRequest
     * @see ApiResponse
     */
    ApiResponse<String> add(AddBookmarkRequest request);

    /**
     * Removes a bookmark based on the bookmark ID.
     *
     * @param bookmarkId The ID of the bookmark to remove.
     * @return ApiResponse indicating the success of the operation.
     *
     * @see ApiResponse
     */
    ApiResponse<String> remove(String bookmarkId);

    /**
     * Retrieves all bookmarks associated with the current user.
     *
     * @return ApiResponse containing a list of BookmarkResponse objects.
     *
     * @see BookmarkResponse
     * @see ApiResponse
     */
    ApiResponse<List<BookmarkResponse>> bookmarks();
}