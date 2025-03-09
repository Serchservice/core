package com.serch.server.core.file.requests;

import lombok.Data;

@Data
public class UploadRequest {
    private String type;
    private String id;
    private String folder;
    private FileUploadRequest upload;
}