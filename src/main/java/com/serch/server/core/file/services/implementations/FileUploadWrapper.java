package com.serch.server.core.file.services.implementations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.serch.server.core.file.data.FileUpload;
import com.serch.server.core.file.data.FileUploadAccount;
import com.serch.server.core.file.data.repositories.FileUploadAccountRepository;
import com.serch.server.core.file.data.repositories.FileUploadMapper;
import com.serch.server.core.file.data.repositories.FileUploadRepository;
import com.serch.server.core.file.requests.FileUploadRequest;
import com.serch.server.core.file.responses.CloudinaryResponse;
import com.serch.server.core.file.responses.FileUploadResponse;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.models.auth.User;
import com.serch.server.domains.nearby.models.go.GoBCap;
import com.serch.server.domains.nearby.repositories.go.GoUserRepository;
import com.serch.server.utils.TimeUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * {@code FileUploadWrapper} is a component class designed to encapsulate file upload operations
 * to Cloudinary, along with related database interactions and post-processing tasks.
 * It provides a fluent API to upload, delete, and manage file metadata, catering to various
 * file types and associated entity relationships within the application.
 *
 * <p>This class abstracts the complexity of Cloudinary API interactions and database persistence,
 * offering a simplified interface for file management. It supports different upload scenarios,
 * including basic uploads, uploads with entity association, and uploads followed by entity updates.
 *
 * <p>It leverages repositories for {@link FileUploadAccount}, {@link FileUpload}, and {@link GoUser}
 * to manage data persistence. It also uses {@link Cloudinary} for cloud-based file storage and
 * retrieval.
 *
 * <p>The class provides methods to:
 * <ul>
 * <li>Initialize upload parameters with file, folder, and entity details.</li>
 * <li>Upload files to Cloudinary and store metadata.</li>
 * <li>Delete files from Cloudinary and the database.</li>
 * <li>Cache upload results and associate them with entities.</li>
 * <li>Update entity attributes based on successful uploads.</li>
 * <li>Handle file type detection and naming conventions.</li>
 * </ul>
 *
 * <p>It also includes utility methods for file type detection and generating unique file names.
 */
@Component
public class FileUploadWrapper {

    private static final Logger log = LoggerFactory.getLogger(FileUploadWrapper.class);

    private final Cloudinary cloudinary;
    private final FileUploadAccountRepository fileUploadAccountRepository;
    private final FileUploadRepository fileUploadRepository;
    private final GoUserRepository goUserRepository;

    /**
     * Stores the {@link FileUploadResponse} object, providing details about the uploaded file.
     * This response includes the file's URL, type, size, and duration.
     */
    @Getter
    private FileUploadResponse response;

    /**
     * Stores arbitrary data associated with the upload, typically an entity
     * like {@link User}, {@link GoActivity}, or {@link GoBCap}. This allows for
     * context-specific processing after the upload.
     */
    @Getter
    private Object data;

    /**
     * Stores the raw {@link CloudinaryResponse} object, which contains detailed
     * information returned by the Cloudinary API after a successful upload.
     */
    @Getter
    private CloudinaryResponse details;

    private FileUploadRequest file;
    private String folder;
    private String type;
    private String id;

    /**
     * Constructs a new {@code FileUploadWrapper} with the specified dependencies.
     *
     * @param cloudinary                The {@link Cloudinary} instance for file uploads.
     * @param fileUploadAccountRepository The repository for managing {@link FileUploadAccount}.
     * @param fileUploadRepository      The repository for managing {@link FileUpload}.
     * @param goUserRepository          The repository for managing {@link GoUser}.
     */
    @Autowired
    public FileUploadWrapper(
            Cloudinary cloudinary,
            FileUploadAccountRepository fileUploadAccountRepository,
            FileUploadRepository fileUploadRepository,
            GoUserRepository goUserRepository
    ) {
        this.cloudinary = cloudinary;
        this.fileUploadAccountRepository = fileUploadAccountRepository;
        this.fileUploadRepository = fileUploadRepository;
        this.goUserRepository = goUserRepository;
    }

    /**
     * Initializes the {@code FileUploadWrapper} with the given file, folder, entity ID, and type.
     * This method prepares the wrapper for an upload operation associated with a specific entity.
     *
     * @param file   The {@link FileUploadRequest} object representing the file to be uploaded.
     * @param folder The desired folder in Cloudinary for storing the uploaded file.
     * @param id     The ID of the entity associated with the file upload.
     * @param type   The type of the entity associated with the file upload.
     * @return This {@code FileUploadWrapper} instance for method chaining.
     */
    public FileUploadWrapper upload(FileUploadRequest file, String folder, String id, String type) {
        registerDependencies(file, folder, id, type);
        return upload();
    }

    /**
     * Initializes the {@code FileUploadWrapper} with the given file, folder, and associated data.
     * This method prepares the wrapper for an upload operation and stores the associated data
     * for post-processing.
     *
     * @param <T>    The type of the associated data.
     * @param data   The data to be associated with the file upload.
     * @param file   The {@link FileUploadRequest} object representing the file to be uploaded.
     * @param folder The desired folder in Cloudinary for storing the uploaded file.
     * @return This {@code FileUploadWrapper} instance for method chaining.
     */
    public <T> FileUploadWrapper upload(FileUploadRequest file, String folder, T data) {
        registerDependencies(file, folder, data);
        return upload();
    }

    /**
     * Initializes the {@code FileUploadWrapper} with the given file, folder, entity ID, and type,
     * and then performs an upload followed by an entity update.
     *
     * @param file   The {@link FileUploadRequest} object representing the file to be uploaded.
     * @param folder The desired folder in Cloudinary for storing the uploaded file.
     * @param id     The ID of the entity associated with the file upload.
     * @param type   The type of the entity associated with the file upload.
     * @return This {@code FileUploadWrapper} instance for method chaining.
     */
    public FileUploadWrapper uploadAndPut(FileUploadRequest file, String folder, String id, String type) {
        registerDependencies(file, folder, id, type);
        return uploadAndPut();
    }

    /**
     * Initializes the {@code FileUploadWrapper} with the given file, folder, and associated data,
     * and then performs an upload followed by an entity update.
     *
     * @param <T>    The type of the associated data.
     * @param data   The data to be associated with the file upload.
     * @param file   The {@link FileUploadRequest} object representing the file to be uploaded.
     * @param folder The desired folder in Cloudinary for storing the uploaded file.
     * @return This {@code FileUploadWrapper} instance for method chaining.
     */
    public <T> FileUploadWrapper uploadAndPut(FileUploadRequest file, String folder, T data) {
        registerDependencies(file, folder, data);
        return uploadAndPut();
    }

    /**
     * Deletes a file from Cloudinary and the database by its ID.
     *
     * @param id The ID of the file to delete.
     * @return This {@code FileUploadWrapper} instance for method chaining.
     */
    public FileUploadWrapper delete(String id) {
        fileUploadRepository.findByAssetIdOrPublicId(id).ifPresent(upload -> {
            if (fileUploadRepository.existsByAssetId(id)) {
                try {
                    cloudinary.uploader().destroy(upload.getAssetId(), ObjectUtils.emptyMap());
                } catch (IOException e) {
                    log.error(e.getMessage(), e);

                    throw new SerchException("An error occurred while deleting your %s".formatted(getType(file.getPath())));
                }
            } else if (fileUploadRepository.existsByPublicId(id)) {
                try {
                    cloudinary.uploader().destroy(upload.getPublicId(), ObjectUtils.emptyMap());
                } catch (IOException e) {
                    log.error(e.getMessage(), e);

                    throw new SerchException("An error occurred while deleting your %s".formatted(getType(file.getPath())));
                }
            }

            fileUploadRepository.delete(upload);
        });

        return this;
    }

    /**
     * Registers the dependencies required for a file upload operation when the upload is associated
     * with a specific entity identified by an ID and type. This method initializes the file, folder,
     * entity ID, and type, and resets the response, details, and data fields.
     *
     * @param file   The {@link FileUploadRequest} object representing the file to be uploaded.
     * @param folder The desired folder in Cloudinary for storing the uploaded file.
     * @param id     The ID of the entity associated with the file upload.
     * @param type   The type of the entity associated with the file upload.
     */
    private void registerDependencies(FileUploadRequest file, String folder, String id, String type) {
        this.file = file;
        this.folder = folder;
        this.response = null;
        this.details = null;
        this.id = id;
        this.type = type;
        this.data = null;
    }

    /**
     * Registers the dependencies required for a file upload operation when the upload is associated
     * with arbitrary data. This method initializes the file, folder, and data fields, and resets
     * the response and details fields. It also infers the type of the upload based on the folder name
     * or defaults to "avatar" if no specific type can be determined.
     *
     * @param <T>    The type of the associated data.
     * @param file   The {@link FileUploadRequest} object representing the file to be uploaded.
     * @param folder The desired folder in Cloudinary for storing the uploaded file.
     * @param data   The data to be associated with the file upload.
     */
    private <T> void registerDependencies(FileUploadRequest file, String folder, T data) {
        this.file = file;
        this.folder = folder;
        this.response = null;
        this.details = null;
        this.data = data;

        if (folder.startsWith("certificate")) {
            this.type = "certificate";
        }

        register(data);
    }

    private <T> void register(T data) {
        if (data instanceof User) {
            id = ((User) data).getId().toString();

            if(this.type == null) {
                this.type = String.format("%s-avatar", ((User) data).getRole().getType());
            }
        } else if(data instanceof GoActivity) {
            this.type = "go-activity";
            this.id = ((GoActivity) data).getId();
        } else if(data instanceof GoBCap) {
            this.type = "go-bcap";
            this.id = ((GoBCap) data).getId();
        }
    }

    /**
     * Uploads the file to Cloudinary and processes the response. This method constructs upload
     * parameters, uploads the file, parses the Cloudinary response, and creates a
     * {@link FileUploadResponse} object.
     *
     * @return This {@code FileUploadWrapper} instance for method chaining.
     * @throws SerchException If an error occurs during the upload process.
     */
    @SuppressWarnings("unchecked")
    private FileUploadWrapper uploader() {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("asset_folder", folder);
            params.put("use_asset_folder_as_public_id_prefix", true);

            if (isVideo(file.getPath()) || isAudio(file.getPath()) || isImage(file.getPath())) {
                params.put("display_name", generateName(file.getPath()));
            } else {
                params.put("use_filename_as_display_name", true);
            }

            if(isVideo(file.getPath())) {
                params.put("resource_type", "video");
            }

            var result = cloudinary.uploader().upload(file.get(), params);
            CloudinaryResponse cloudResponse = CloudinaryResponse.fromJson(result);

            details = cloudResponse;
            response = toResponse(cloudResponse.getSecureUrl(), file);
            response.setPublicId(cloudResponse.getPublicId());
            response.setAssetId(cloudResponse.getAssetId());

            return this;
        } catch (IOException e) {
            log.error(e.getMessage(), e);

            throw new SerchException("An error occurred while uploading your %s".formatted(getType(file.getPath())));
        }
    }

    /**
     * Creates a {@link FileUploadResponse} object from the given {@link FileUploadRequest}.
     * This method populates the response object with file details such as path, duration,
     * size, and type.
     *
     * @param file The {@link FileUploadRequest} object.
     * @return A {@link FileUploadResponse} object.
     */
    private FileUploadResponse toResponse(String result, FileUploadRequest file) {
        FileUploadResponse response = new FileUploadResponse();
        response.setFile(result);
        response.setDuration(file.getDuration());
        response.setSize(file.getSize());
        response.setType(getType(file.getPath()));

        return response;
    }

    /**
     * Uploads the file and caches the result. This method calls the {@code uploader()} method
     * to upload the file and then calls the {@code cache()} method to persist the upload details.
     *
     * @return This {@code FileUploadWrapper} instance for method chaining.
     */
    private FileUploadWrapper upload() {
        FileUploadWrapper result = uploader();
        result.cache();

        return result;
    }

    /**
     * Uploads the file, caches the result, and updates the associated entity. This method calls
     * the {@code uploader()} method to upload the file, then calls the {@code cache()} method
     * to persist the upload details, and finally updates the entity using the provided action.
     *
     * @return This {@code FileUploadWrapper} instance for method chaining.
     */
    private FileUploadWrapper uploadAndPut() {
        return uploader().cache(update -> {
            if (details != null) {
                if (type.equalsIgnoreCase("nearby")) {
                    goUserRepository.findById(UUID.fromString(id)).ifPresent(user -> {
                        user.setAvatar(update.getFile());
                        user.setUpdatedAt(TimeUtil.now());
                        goUserRepository.save(user);
                    });
                }
            }
        });
    }

    /**
     * Caches the upload result by persisting the file upload details to the database.
     * This method associates the upload with an entity account and saves the upload details.
     *
     * @return The persisted {@link FileUpload} object, or {@code null} if caching fails.
     */
    private FileUpload cache() {
        if (response != null && details != null && type != null && !type.isEmpty() && id != null && !id.isEmpty()) {
            FileUpload upload = FileUploadMapper.instance.upload(details);
            upload.setSize(response.getSize());
            upload.setType(response.getType());
            upload.setDuration(response.getDuration());
            upload.setFile(response.getFile());
            upload.setAccount(getFileUploadAccount());
            upload = fileUploadRepository.save(upload);

            return upload;
        }

        return null;
    }

    /**
     * Creates and persists a {@link FileUploadAccount} object associated with the upload.
     *
     * @return The persisted {@link FileUploadAccount} object.
     */
    private FileUploadAccount getFileUploadAccount() {
        FileUploadAccount account = new FileUploadAccount();
        account.setType(type);
        account.setAccount(id);

        return fileUploadAccountRepository.save(account);
    }

    /**
     * Caches the upload result and executes the provided action on the persisted
     * {@link FileUpload} object.
     *
     * @param action The action to be executed with the persisted {@link FileUpload} object.
     * @return This {@code FileUploadWrapper} instance for method chaining.
     */
    private FileUploadWrapper cache(Consumer<FileUpload> action) {
        FileUpload upload = cache();
        if (upload != null) {
            action.accept(upload);
        }

        return this;
    }

    // Utility methods for file type detection (same as your original implementation)
    /**
     * Generates a unique identifier for the uploaded file.
     *
     * @return The generated unique identifier.
     */
    private String generateName(String path) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);

        String prefix = "S"; // Default prefix
        if (isVideo(path)) {
            prefix += "VID";
        } else {
            prefix += "IMG";
        }

        return String.format("%s-%s-%s", prefix, timestamp, uniqueId);
    }

    /**
     * Determines the file type based on the file extension.
     *
     * @param path The file path.
     * @return The file type (e.g., "image", "video", "audio", "document", "other").
     */
    private String getType(String path) {
        String ext = path.toLowerCase();

        if (isVideo(ext)) {
            return "video";
        } else if (isImage(ext)) {
            return "photo";
        } else if(isVector(ext)) {
            return "svg";
        } else if (isAudio(ext)) {
            return "audio";
        } else if (isDocument(ext)) {
            return "document";
        } else if(isAPK(ext)) {
            return "apk";
        } else if(isHTML(ext)) {
            return "html";
        } else {
            return "other";
        }
    }

    /**
     * Checks if the given file extension is a video file extension.
     *
     * @param ext The file extension (e.g., ".mp4", ".avi").
     * @return `true` if the extension is a video extension, `false` otherwise.
     */
    private boolean isVideo(String ext) {
        return ext.endsWith(".mp4") ||
                ext.endsWith(".avi") ||
                ext.endsWith(".wmv") ||
                ext.endsWith(".rmvb") ||
                ext.endsWith(".mpg") ||
                ext.endsWith(".mpeg") ||
                ext.endsWith(".3gp");
    }

    /**
     * Checks if the given file extension is an image file extension.
     *
     * @param ext The file extension (e.g., ".jpg", ".png").
     * @return `true` if the extension is an image extension, `false` otherwise.
     */
    private boolean isImage(String ext) {
        return ext.endsWith(".jpg") ||
                ext.endsWith(".jpeg") ||
                ext.endsWith(".png") ||
                ext.endsWith(".gif") ||
                ext.endsWith(".bmp");
    }

    /**
     * Checks if the given file extension is an audio file extension.
     *
     * @param ext The file extension (e.g., ".mp3", ".wav").
     * @return `true` if the extension is an audio extension, `false` otherwise.
     */
    private boolean isAudio(String ext) {
        return ext.endsWith(".mp3") ||
                ext.endsWith(".wav") ||
                ext.endsWith(".wma") ||
                ext.endsWith(".amr") ||
                ext.endsWith(".ogg");
    }

    /**
     * Checks if the given file extension is a document file extension.
     *
     * @param ext The file extension (e.g., ".pdf", ".doc", ".docx").
     * @return `true` if the extension is a document extension, `false` otherwise.
     */
    private boolean isDocument(String ext) {
        return isPDF(ext) || isPPT(ext) || isWord(ext) || isExcel(ext) || isTxt(ext) || isChm(ext);
    }

    /**
     * Checks if the given file extension is a PowerPoint file extension.
     *
     * @param ext The file extension (e.g., ".ppt", ".pptx").
     * @return `true` if the extension is a PowerPoint extension, `false` otherwise.
     */
    private boolean isPPT(String ext) {
        return ext.endsWith(".ppt") || ext.endsWith(".pptx");
    }

    /**
     * Checks if the given file extension is a Word file extension.
     *
     * @param ext The file extension (e.g., ".doc", ".docx").
     * @return `true` if the extension is a Word extension, `false` otherwise.
     */
    private boolean isWord(String ext) {
        return ext.endsWith(".doc") || ext.endsWith(".docx");
    }

    /**
     * Checks if the given file extension is an Excel file extension.
     *
     * @param ext The file extension (e.g., ".xls", ".xlsx").
     * @return `true` if the extension is an Excel extension, `false` otherwise.
     */
    private boolean isExcel(String ext) {
        return ext.endsWith(".xls") || ext.endsWith(".xlsx");
    }

    /**
     * Checks if the given file extension is an APK file extension.
     *
     * @param ext The file extension (e.g., ".apk").
     * @return `true` if the extension is an APK extension, `false` otherwise.
     */
    private boolean isAPK(String ext) {
        return ext.toLowerCase().endsWith(".apk");
    }

    /**
     * Checks if the given file extension is a PDF file extension.
     *
     * @param ext The file extension (e.g., ".pdf").
     * @return `true` if the extension is a PDF extension, `false` otherwise.
     */
    private boolean isPDF(String ext) {
        return ext.toLowerCase().endsWith(".pdf");
    }

    /**
     * Checks if the given file extension is a TXT file extension.
     *
     * @param ext The file extension (e.g., ".txt").
     * @return `true` if the extension is a TXT extension, `false` otherwise.
     */
    private boolean isTxt(String ext) {
        return ext.toLowerCase().endsWith(".txt");
    }

    /**
     * Checks if the given file extension is a CHM file extension.
     *
     * @param ext The file extension (e.g., ".chm").
     * @return `true` if the extension is a CHM extension, `false` otherwise.
     */
    private boolean isChm(String ext) {
        return ext.toLowerCase().endsWith(".chm");
    }

    /**
     * Checks if the given file extension is a vector file extension.
     *
     * @param ext The file extension (e.g., ".svg").
     * @return `true` if the extension is a vector extension, `false` otherwise.
     */
    private boolean isVector(String ext) {
        return ext.toLowerCase().endsWith(".svg");
    }

    /**
     * Checks if the given file extension is an HTML file extension.
     *
     * @param ext The file extension (e.g., ".html").
     * @return `true` if the extension is an HTML extension, `false` otherwise.
     */
    private boolean isHTML(String ext) {
        return ext.toLowerCase().endsWith(".html");
    }
}
