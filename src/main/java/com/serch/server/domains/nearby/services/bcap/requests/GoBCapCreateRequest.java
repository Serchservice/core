package com.serch.server.domains.nearby.services.bcap.requests;

import com.serch.server.core.file.requests.FileUploadRequest;
import lombok.Data;

import java.util.List;

@Data
public class GoBCapCreateRequest {
    private String id;
    private String description;
    private List<FileUploadRequest> media;
}