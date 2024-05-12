package com.serch.server.services.account.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.Gender;
import com.serch.server.services.auth.requests.RequestPhoneInformation;
import com.serch.server.services.supabase.requests.FileUploadRequest;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private Gender gender;
    private RequestPhoneInformation phone;
    private FileUploadRequest upload;
}