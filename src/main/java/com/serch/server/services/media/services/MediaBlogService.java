package com.serch.server.services.media.services;


import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaBlogResponse;

import java.util.List;

public interface MediaBlogService {
    ApiResponse<MediaBlogResponse> findBlog(String key);
    ApiResponse<List<MediaBlogResponse>> findAllBlogs(Integer page);
}
