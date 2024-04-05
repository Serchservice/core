package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaAssetResponse;

import java.util.List;

public interface MediaAssetService {
    ApiResponse<List<MediaAssetResponse>> fetchAllAssets();
}