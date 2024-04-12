package com.serch.server.services.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateGuestRequest {
    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("fcm_token")
    private String fcmToken;

    private String gender;
    private String link;
    private MultipartFile avatar;
    private String platform;
}
