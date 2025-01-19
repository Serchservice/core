package com.serch.server.core.storage.services;

import com.serch.server.core.storage.requests.FileUploadRequest;

/**
 * Service interface for handling file storage operations.
 * <p>
 * This interface defines methods for uploading files to Supabase storage
 * and constructing URLs for accessing those files. Implementations of this
 * interface should handle the interactions with Supabase or any other
 * designated storage service.
 * </p>
 */
public interface StorageService {

    /**
     * Uploads the specified file to Supabase storage.
     * <p>
     * This method accepts a file upload request and the name of the bucket
     * where the file should be stored. Upon successful upload, it returns
     * the public URL that can be used to access the uploaded file.
     * </p>
     *
     * @param request The {@link FileUploadRequest} containing the details
     *                of the file to be uploaded, including metadata such as
     *                file name, type, and byte content.
     * @param bucket  The name of the bucket in Supabase storage where the
     *                file will be saved. The bucket must exist prior to
     *                uploading the file.
     *
     * @return A {@link String} representing the public URL of the uploaded
     *         file, which can be used for accessing the file over the internet.
     */
    String upload(FileUploadRequest request, String bucket);

    /**
     * Constructs the full URL for a Supabase item.
     * <p>
     * This method takes a relative URL and appends the necessary prefix or
     * base URL to create a complete URL that can be used to access the item.
     * </p>
     *
     * @param url The relative URL to append the base URL to.
     *
     * @return A {@link String} representing the full URL for the Supabase item,
     *         allowing direct access to the stored file.
     */
    String buildUrl(String url);
}