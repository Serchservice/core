package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaLegalGroupResponse;
import com.serch.server.services.media.responses.MediaLegalResponse;

import java.util.List;

public interface MediaLegalService {
    ApiResponse<List<MediaLegalGroupResponse>> fetchAllLegals();
    ApiResponse<MediaLegalResponse> fetchLegal(String key);
}
