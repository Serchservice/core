package com.serch.server.services.storage.core;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.StorageException;
import com.serch.server.services.storage.requests.UploadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service implementation class for managing file storage operations. It provides methods
 * for uploading files to a cloud storage service and retrieving file extensions.
 *
 * @see StorageService
 * @see Cloudinary
 * @see UploadRequest
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageImplementation implements StorageService {
    @Value("${application.storage.name}")
    private String STORAGE_NAME;
    @Value("${application.storage.api.key}")
    private String STORAGE_API_KEY;
    @Value("${application.storage.api.secret}")
    private String STORAGE_API_SECRET;

    private final Cloudinary cloudinary = new Cloudinary(
            "cloudinary://%s:%s@%s".formatted(STORAGE_API_KEY, STORAGE_API_SECRET, STORAGE_NAME)
    );

    /**
     * Retrieves the extension of the uploaded file.
     *
     * @param request The upload request containing the file.
     * @return The extension of the uploaded file.
     * @throws StorageException if the file extension is not found.
     */
    @Override
    public String getExtension(UploadRequest request) {
        String file = request.getFile().getOriginalFilename();

        if (file != null) {
            int dotIndex = file.lastIndexOf(".");
            if (dotIndex >= 0) {
                return file.substring(dotIndex);
            }
        } else {
            return StringUtils.getFilenameExtension(request.getFile().getName());
        }
        throw new StorageException("File extension not found");
    }

    private String generateName() {
        return "SerchStore-%s%s%s%s%s%s%s".formatted(
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonthValue(),
                LocalDateTime.now().getDayOfMonth(),
                LocalDateTime.now().getHour(),
                LocalDateTime.now().getMinute(),
                LocalDateTime.now().getSecond(),
                UUID.randomUUID().toString().substring(0, 10).replaceAll("-", "")
        );
    }

    /**
     * Uploads a file to the cloud storage service.
     *
     * @param request The upload request containing the file.
     * @return A response containing the URL of the uploaded file.
     * @throws StorageException if an error occurs during the upload process.
     */
    @Override
    public ApiResponse<String> upload(UploadRequest request) {
        String name = generateName();

        try {
            cloudinary.uploader().upload(
                    request.getFile().getBytes(),
                    ObjectUtils.asMap("public_id", name)
            );

            return new ApiResponse<>(
                    cloudinary.url().generate("%s.%s".formatted(name, getExtension(request))),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }
}