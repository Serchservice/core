package com.serch.server.core.file.requests;

import lombok.Data;

@Data
public class FileUploadRequest {
    private String path;
    private String media;
    private String size;
    private String duration;
    private byte[] bytes;
    private byte[] data;

    public byte[] get() {
        if(getData() != null) {
            return getData();
        }

        return getBytes();
    }
}