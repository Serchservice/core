package com.serch.server.services.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdditionalInformationResponse {
    @JsonProperty("street_address")
    private String streetAddress;

    @JsonProperty("land_mark")
    private String landMark;

    private String city;
    private String state;
    private String country;

    @JsonProperty("surety_status")
    private String suretyStatus;

    @JsonProperty("surety_first_name")
    private String suretyFirstName;

    @JsonProperty("surety_last_name")
    private String suretyLastName;

    @JsonProperty("surety_email_address")
    private String suretyEmail;

    @JsonProperty("surety_phone_number")
    private String suretyPhone;

    @JsonProperty("surety_address")
    private String suretyAddress;
}
