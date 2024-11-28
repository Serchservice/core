package com.serch.server.core.storage.core;

import com.serch.server.enums.ServerHeader;
import com.serch.server.exceptions.others.StorageException;
import com.serch.server.core.storage.requests.FileUploadRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Storage implements StorageService {
    private static final Logger log = LoggerFactory.getLogger(Storage.class);

    @Value("${application.supabase.api-key}")
    private String API_KEY;
    @Value("${application.supabase.base-url}")
    private String BASE_URL;

    private final RestTemplate rest;

    /**
     * Constructs and returns HTTP headers for making API requests.
     *
     * @return HttpHeaders containing the necessary headers for API requests.
     */
    private HttpHeaders headers(MediaType content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(content);
        headers.add(ServerHeader.SUPABASE_API_KEY.getValue(), API_KEY);
        headers.add(ServerHeader.AUTHORIZATION.getValue(), "Bearer "+ API_KEY);
        return headers;
    }

    public String upload(FileUploadRequest request, String bucket) {
        String extension = StringUtils.getFilenameExtension(request.getPath());
        MediaType content = mediaType().get(extension);
        if (content == null) {
            throw new StorageException(String.format("Unsupported file extension for %s", extension));
        } else {
            String filename = getUploadFilename(request, extension);

            HttpEntity<byte[]> entity = new HttpEntity<>(request.getBytes(), headers(content));
            String endpoint = buildUrl(String.format("/storage/v1/object/%s/%s", bucket, filename));
            try {
                ResponseEntity<Object> response = rest.exchange(endpoint, HttpMethod.POST, entity, Object.class);

                if(response.getStatusCode().is2xxSuccessful()) {
                    return buildUrl(String.format("/storage/v1/object/public/%s/%s", bucket, filename));
                } else {
                    throw new StorageException("Couldn't upload file");
                }
            } catch (Exception ignored) {
                try {
                    ResponseEntity<Object> response = rest.exchange(endpoint, HttpMethod.PUT, entity, Object.class);

                    if(response.getStatusCode().is2xxSuccessful()) {
                        return buildUrl(String.format("/storage/v1/object/public/%s/%s", bucket, filename));
                    } else {
                        throw new StorageException("Couldn't upload file");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());

                    throw new StorageException("Operation timed out. Try again later");
                }
            }
        }
    }

    private String getUploadFilename(FileUploadRequest request, String extension) {
        String name = request.getMedia().equalsIgnoreCase("PDF") ? request.getPath() : String.format(
                "%s-%s%s%s%s%s%s%s",
                request.getMedia().equalsIgnoreCase("VIDEO") ? "SVID" : "SIMG",
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonthValue(),
                LocalDateTime.now().getDayOfMonth(),
                LocalDateTime.now().getHour(),
                LocalDateTime.now().getMinute(),
                LocalDateTime.now().getSecond(),
                UUID.randomUUID().toString().substring(0, 10).replaceAll("-", "")
        );

        return request.getMedia().equalsIgnoreCase("PDF") ? request.getPath() : String.format("%s.%s", name, extension);
    }

    @Override
    public String buildUrl(String url) {
        return String.format("%s%s", BASE_URL, url);
    }

    private Map<String, MediaType> mediaType() {
        Map<String, MediaType> CONTENT_TYPE_MAP = new HashMap<>();
        // Image types
        CONTENT_TYPE_MAP.put("png", MediaType.IMAGE_PNG);
        CONTENT_TYPE_MAP.put("jpg", MediaType.IMAGE_JPEG);
        CONTENT_TYPE_MAP.put("jpeg", MediaType.IMAGE_JPEG);
        CONTENT_TYPE_MAP.put("gif", MediaType.IMAGE_GIF);
        CONTENT_TYPE_MAP.put("bmp", MediaType.parseMediaType("image/bmp"));
        CONTENT_TYPE_MAP.put("svg", MediaType.parseMediaType("image/svg+xml"));

        // Video types
        CONTENT_TYPE_MAP.put("mp4", MediaType.parseMediaType("video/mp4"));
        CONTENT_TYPE_MAP.put("mkv", MediaType.parseMediaType("video/x-matroska"));
        CONTENT_TYPE_MAP.put("avi", MediaType.parseMediaType("video/x-msvideo"));
        CONTENT_TYPE_MAP.put("mov", MediaType.parseMediaType("video/quicktime"));
        CONTENT_TYPE_MAP.put("wmv", MediaType.parseMediaType("video/x-ms-wmv"));
        CONTENT_TYPE_MAP.put("flv", MediaType.parseMediaType("video/x-flv"));
        CONTENT_TYPE_MAP.put("webm", MediaType.parseMediaType("video/webm"));

        // Audio types
        CONTENT_TYPE_MAP.put("mp3", MediaType.parseMediaType("audio/mpeg"));
        CONTENT_TYPE_MAP.put("wav", MediaType.parseMediaType("audio/wav"));
        CONTENT_TYPE_MAP.put("ogg", MediaType.parseMediaType("audio/ogg"));
        CONTENT_TYPE_MAP.put("aac", MediaType.parseMediaType("audio/aac"));
        CONTENT_TYPE_MAP.put("flac", MediaType.parseMediaType("audio/flac"));
        CONTENT_TYPE_MAP.put("m4a", MediaType.parseMediaType("audio/mp4"));

        // Document types
        CONTENT_TYPE_MAP.put("pdf", MediaType.APPLICATION_PDF);
        CONTENT_TYPE_MAP.put("doc", MediaType.parseMediaType("application/msword"));
        CONTENT_TYPE_MAP.put("docx", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        CONTENT_TYPE_MAP.put("xls", MediaType.parseMediaType("application/vnd.ms-excel"));
        CONTENT_TYPE_MAP.put("xlsx", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        CONTENT_TYPE_MAP.put("ppt", MediaType.parseMediaType("application/vnd.ms-powerpoint"));
        CONTENT_TYPE_MAP.put("pptx", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        CONTENT_TYPE_MAP.put("txt", MediaType.TEXT_PLAIN);

        // Archive types
        CONTENT_TYPE_MAP.put("zip", MediaType.parseMediaType("application/zip"));
        CONTENT_TYPE_MAP.put("rar", MediaType.parseMediaType("application/x-rar-compressed"));
        CONTENT_TYPE_MAP.put("tar", MediaType.parseMediaType("application/x-tar"));
        CONTENT_TYPE_MAP.put("gz", MediaType.parseMediaType("application/gzip"));
        return CONTENT_TYPE_MAP;
    }
}
