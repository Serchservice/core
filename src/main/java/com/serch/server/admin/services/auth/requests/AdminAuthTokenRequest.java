package com.serch.server.admin.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.enums.UserAction;
import com.serch.server.services.auth.requests.RequestDevice;
import lombok.Data;

@Data
public class AdminAuthTokenRequest {
    private String token;

    @JsonProperty("email_address")
    private String emailAddress;
    private UserAction action;
    private String state;
    private String country;
    private RequestDevice device;
}