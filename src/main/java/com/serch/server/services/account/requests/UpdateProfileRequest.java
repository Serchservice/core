package com.serch.server.services.account.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.auth.requests.RequestPhoneInformation;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateProfileRequest {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private MultipartFile avatar;
    private RequestPhoneInformation phone;
}
