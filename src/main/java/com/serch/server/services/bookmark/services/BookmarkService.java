package com.serch.server.services.bookmark.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.bookmark.AddBookmarkRequest;
import com.serch.server.services.bookmark.BookmarkResponse;

import java.util.List;

public interface BookmarkService {
    ApiResponse<String> add(AddBookmarkRequest request);
    ApiResponse<String> remove(String bookmarkId);
    ApiResponse<List<BookmarkResponse>> bookmarks();
}
