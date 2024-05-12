package com.serch.server.services.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.services.auth.requests.RequestPhoneInformation;
import com.serch.server.services.business.responses.BusinessInformationData;
import com.serch.server.services.company.responses.SpecialtyKeywordResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProfileResponse {
    private UUID id;
    private String category;
    private String image;
    private String gender;
    private String status;
    private String avatar;
    private String certificate;
    private Double rating;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("phone_info")
    private RequestPhoneInformation phoneInfo;

    @JsonProperty("verification_status")
    private VerificationStatus verificationStatus;

    @JsonProperty("business_information")
    private BusinessInformationData businessInformation;

    private MoreProfileData more;

    private List<SpecialtyKeywordResponse> specializations;
}
