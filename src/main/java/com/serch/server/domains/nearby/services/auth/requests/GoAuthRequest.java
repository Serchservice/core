package com.serch.server.domains.nearby.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.core.location.responses.Address;
import com.serch.server.domains.auth.requests.RequestDevice;
import lombok.Data;

@Data
public class GoAuthRequest {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email_address")
    private String emailAddress;

    private String password;
    private String contact;
    private String timezone;

    @JsonProperty("fcm_token")
    private String fcmToken;

    private Address address;
    private RequestDevice device;
}