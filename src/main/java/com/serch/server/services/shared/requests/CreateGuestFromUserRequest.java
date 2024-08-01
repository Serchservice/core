package com.serch.server.services.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.auth.requests.RequestDevice;
import com.serch.server.core.storage.requests.FileUploadRequest;
import lombok.Data;

@Data
public class CreateGuestFromUserRequest {
    @JsonProperty("email_address")
    private String emailAddress;
    private String link;
    private String password;
    private String country;
    private String state;
    private FileUploadRequest upload;
    private RequestDevice device;
}