package com.serch.server.core.file.services;

import com.serch.server.core.file.requests.FileUploadRequest;
import com.serch.server.core.file.responses.FileUploadResponse;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.models.auth.User;
import com.serch.server.domains.nearby.models.go.GoBCap;

import java.util.UUID;

/**
 * This interface defines the contract for file upload operations.
 * It provides methods for uploading files and retrieving file information.
 */
public interface FileService {
    /**
     * Uploads a file and returns information about the uploaded file.
     *
     * @param user The {@link User} data of the user uploading the file, used for folder determination.
     * @param file The {@link FileUploadRequest} object containing file details.
     * @return A {@link FileUploadResponse} object containing information about the uploaded file.
     */
    FileUploadResponse uploadCommon(FileUploadRequest file, User user);

    /**
     * Uploads a file and returns information about the uploaded file. Commonly used for certificates.
     *
     * @param user The {@link User} data of the user uploading the file, used for folder determination.
     * @param file The {@link FileUploadRequest} object containing file details.
     * @return A {@link FileUploadResponse} object containing information about the uploaded file.
     */
    FileUploadResponse uploadCertificate(FileUploadRequest file, User user);

    /**
     * Uploads a file and returns information about the uploaded file.
     * Used for guest file uploads.
     *
     * @param emailAddress The emailAddress of the {@link com.serch.server.models.shared.Guest}.
     * @param file The {@link FileUploadRequest} object containing file details.
     * @return A {@link FileUploadResponse} object containing information about the uploaded file.
     */
    FileUploadResponse guest(FileUploadRequest file, String emailAddress);

    /**
     * Uploads a file and returns information about the uploaded file.
     * Used for trip file uploads.
     *
     * @param id The id of the {@link com.serch.server.models.trip.Trip} or {@link com.serch.server.models.trip.TripInvite}
     * @param file The {@link FileUploadRequest} object containing file details.
     * @return A {@link FileUploadResponse} object containing information about the uploaded file.
     */
    FileUploadResponse uploadTrip(FileUploadRequest file, String id);

    /**
     * Uploads a file and returns information about the uploaded file.
     * Used for shop file uploads.
     *
     * @param id The id of the {@link com.serch.server.models.shop.Shop}
     * @param file The {@link FileUploadRequest} object containing file details.
     * @return A {@link FileUploadResponse} object containing information about the uploaded file.
     */
    FileUploadResponse uploadShop(FileUploadRequest file, String id);

    /**
     * Uploads a file and returns information about the uploaded file.
     * Used for nearby - go file uploads.
     *
     * @param id The id of the {@link GoUser}
     * @param file The {@link FileUploadRequest} object containing file details.
     * @return A {@link FileUploadResponse} object containing information about the uploaded file.
     */
    FileUploadResponse uploadGo(FileUploadRequest file, UUID id);

    /**
     * Uploads a file and returns information about the uploaded file.
     * Used for nearby - go file uploads.
     *
     * @param event The activity details . See {@link GoActivity}
     * @param file The {@link FileUploadRequest} object containing file details.
     * @return A {@link FileUploadResponse} object containing information about the uploaded file.
     */
    FileUploadResponse uploadGo(FileUploadRequest file, GoActivity event);

    void delete(String id);

    /**
     * Uploads a file and returns information about the uploaded file.
     * Used for nearby - go file uploads.
     *
     * @param cap The recap details . See {@link GoBCap}
     * @param file The {@link FileUploadRequest} object containing file details.
     * @return A {@link FileUploadResponse} object containing information about the uploaded file.
     */
    FileUploadResponse uploadGo(FileUploadRequest file, GoBCap cap);
}