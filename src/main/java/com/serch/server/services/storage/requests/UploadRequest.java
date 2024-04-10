package com.serch.server.services.storage.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
public class UploadRequest {
    private MultipartFile file;
}
