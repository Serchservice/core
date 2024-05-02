package com.serch.server.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestAdditionalInformation {
    @JsonProperty("email_address")
    private String emailAddress;

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

    private RequestDevice device;
}
