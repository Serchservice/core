package com.serch.server.services.business.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateBusinessRequest {
    private MultipartFile logo;
    private String name;
    private String description;
    private String address;
}
