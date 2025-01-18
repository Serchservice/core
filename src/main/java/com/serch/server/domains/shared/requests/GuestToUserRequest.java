package com.serch.server.domains.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.domains.auth.requests.RequestDevice;
import com.serch.server.domains.auth.requests.RequestPhoneInformation;
import lombok.Data;

@Data
public class GuestToUserRequest {
    @JsonProperty("guest_id")
    private String guestId;

    private String password;

    @JsonProperty("link_id")
    private String linkId;

    @JsonProperty(value = "phone_information")
    RequestPhoneInformation phoneInformation;

    private RequestDevice device;
}