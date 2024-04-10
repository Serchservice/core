package com.serch.server.services.storage.core;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.storage.requests.UploadRequest;

public interface StorageService {
    ApiResponse<String> upload(UploadRequest request);
    String getExtension(UploadRequest request);
}
