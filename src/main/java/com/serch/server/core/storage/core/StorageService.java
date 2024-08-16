package com.serch.server.core.storage.core;

import com.serch.server.core.storage.requests.FileUploadRequest;

public interface StorageService {
    /**
     * This will upload the given file to Supabase storage
     *
     * @param request The {@link FileUploadRequest} for file uploading
     * @param bucket The bucket name where the file will be saved
     *
     * @return The Public url for the file
     */
    String upload(FileUploadRequest request, String bucket);

    /**
     * Construct the full url for the supabase item
     *
     * @param url The url to append
     *
     * @return String
     */
    String buildUrl(String url);
}