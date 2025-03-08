package com.serch.server.domains.account.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.Gender;
import com.serch.server.domains.auth.requests.RequestPhoneInformation;
import com.serch.server.core.file.requests.FileUploadRequest;
import lombok.Data;

@Data
public class UpdateBusinessRequest {
    private Gender gender;
    private FileUploadRequest upload;
    private FileUploadRequest logo;
    private RequestPhoneInformation phone;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("business_name")
    private String businessName;

    @JsonProperty("business_description")
    private String businessDescription;

    @JsonProperty("business_address")
    private String businessAddress;

    @JsonProperty("business_contact")
    private String businessContact;
}