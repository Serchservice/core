package com.serch.server.admin.services.organization.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import lombok.Data;

@Data
public class OrganizationResponse {
    private Long id;
    private String username;
    private String avatar;
    private String instagram;
    private String linkedIn = "";
    private String twitter = "";
    private String position;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("qr_code")
    private String qrCode;

    private CommonProfileResponse admin;
}