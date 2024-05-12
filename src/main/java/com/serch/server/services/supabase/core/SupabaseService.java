package com.serch.server.services.supabase.core;

import com.serch.server.services.supabase.requests.FileUploadRequest;
import com.serch.server.services.supabase.responses.Country;

import java.util.List;

public interface SupabaseService {
    /**
     * Get the list of countries from a database
     *
     * @return List of {@link Country}
     *
     * @see Country
     */
    List<Country> getCountries();

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