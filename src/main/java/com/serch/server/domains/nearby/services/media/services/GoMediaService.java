package com.serch.server.domains.nearby.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.file.requests.FileUploadRequest;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.GoBCap;
import com.serch.server.domains.nearby.models.go.GoMedia;
import com.serch.server.domains.nearby.services.activity.requests.GoCreateActivityRequest;

import java.util.List;

/**
 * This interface defines the contract for managing media files.
 * <p></p>
 * It provides methods for uploading media files and deleting existing media.
 */
public interface GoMediaService {
    /**
     * Deletes a media file.
     *
     * @param id The ID of the media file to delete.
     * @return An {@link ApiResponse} containing a success message or an error message.
     */
    ApiResponse<Void> delete(Long id);

    /**
     * Uploads media files associated with a GoActivity.
     *
     * @param request The GoCreateEventRequest containing activity details.
     * @param activity The GoActivity object.
     * @return A list of uploaded GoMedia objects.
     */
    List<GoMedia> upload(GoCreateActivityRequest request, GoActivity activity);

    /**
     * Uploads a list of media files associated with a GoBCap.
     *
     * @param files A list of FileUploadRequest objects containing file details.
     * @param cap The GoBCap object.
     * @return A list of uploaded GoMedia objects.
     */
    List<GoMedia> upload(List<FileUploadRequest> files, GoBCap cap);
}