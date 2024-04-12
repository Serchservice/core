package com.serch.server.services.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.auth.requests.RequestDevice;
import com.serch.server.services.auth.requests.RequestPhoneInformation;
import lombok.Data;

@Data
public class GuestToUserRequest {
    private String guestId;
    private String password;
    private String platform;
    @JsonProperty(value = "phone_information")
    RequestPhoneInformation phoneInformation;
    private RequestDevice device;
}
