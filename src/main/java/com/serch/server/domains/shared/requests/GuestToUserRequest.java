package com.serch.server.domains.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.domains.auth.requests.RequestDevice;
import com.serch.server.domains.auth.requests.RequestPhoneInformation;
import lombok.Data;

@Data
public class GuestToUserRequest {
    private String password;

    @JsonProperty(value = "phone_information")
    RequestPhoneInformation phoneInformation;

    private RequestDevice device;
}