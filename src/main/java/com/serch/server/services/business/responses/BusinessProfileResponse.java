package com.serch.server.services.business.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.services.account.responses.MoreProfileData;
import com.serch.server.services.auth.requests.RequestPhoneInformation;
import com.serch.server.services.account.responses.SpecialtyResponse;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BusinessProfileResponse {
    private UUID id;
    private String name;
    private String description;
    private String address;
    private String contact;
    private String logo;
    private Double rating;
    private String gender;
    private String category;
    private String image;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("default_password")
    private String defaultPassword;

    @JsonProperty("phone_info")
    private RequestPhoneInformation phoneInfo;

    @JsonProperty("verification_status")
    private VerificationStatus verificationStatus;

    private MoreProfileData more;

    private List<SpecialtyResponse> specializations;
}
