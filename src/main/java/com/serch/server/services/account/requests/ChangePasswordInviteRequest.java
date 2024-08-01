package com.serch.server.services.account.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.auth.requests.RequestDevice;
import lombok.Data;

@Data
public class ChangePasswordInviteRequest {
    @JsonProperty("email_address")
    private String emailAddress;

    private String scope;
    private String password;
    private String country;
    private String state;
    private RequestDevice device;
}
