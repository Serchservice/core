package com.serch.server.services.storage.core;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.storage.requests.UploadRequest;

/**
 * Service interface for managing file storage operations.
 *
 * @see StorageImplementation
 * @see UploadRequest
 */
public interface StorageService {

    /**
     * Uploads a file to the storage service.
     *
     * @param request The upload request containing the file.
     * @return A response containing the URL of the uploaded file.
     */
    ApiResponse<String> upload(UploadRequest request);

    /**
     * Retrieves the extension of the uploaded file.
     *
     * @param request The upload request containing the file.
     * @return The extension of the uploaded file.
     */
    String getExtension(UploadRequest request);
}
