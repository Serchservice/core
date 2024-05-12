package com.serch.server.services.business.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.auth.requests.RequestDevice;
import lombok.Data;

@Data
public class ChangePasswordInviteRequest {
    @JsonProperty("email_address")
    private String emailAddress;

    private String scope;
    private String password;
    private RequestDevice device;
}
