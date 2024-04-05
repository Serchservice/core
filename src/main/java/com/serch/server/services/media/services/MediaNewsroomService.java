package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaNewsroomResponse;

import java.util.List;

public interface MediaNewsroomService {
    ApiResponse<MediaNewsroomResponse> findNews(String key);
    ApiResponse<List<MediaNewsroomResponse>> findAllNews(Integer page);
}
