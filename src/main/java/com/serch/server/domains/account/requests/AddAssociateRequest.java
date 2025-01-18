package com.serch.server.domains.account.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.enums.account.Gender;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.verified.ConsentType;
import com.serch.server.domains.auth.requests.RequestPhoneInformation;
import lombok.Data;

import java.util.List;

@Data
public class AddAssociateRequest {
    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    @JsonProperty(value = "email_address")
    private String emailAddress;

    @JsonProperty(value = "phone_information")
    private RequestPhoneInformation phoneInfo;

    @SerchEnum(message = "Gender must be an enum")
    private Gender gender;

    private SerchCategory category;
    private List<String> specialties;
    private ConsentType consent;
}