package com.serch.server.services.supabase.requests;

import lombok.Data;

@Data
public class FileUploadRequest {
    private String path;
    private String media;
    private byte[] bytes;
}