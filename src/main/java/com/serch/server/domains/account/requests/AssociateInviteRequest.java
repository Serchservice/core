package com.serch.server.domains.account.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.domains.auth.requests.RequestDevice;
import lombok.Data;

@Data
public class AssociateInviteRequest {
    @JsonProperty("email_address")
    private String emailAddress;

    private String scope;
    private String password;
    private String country;
    private String state;
    private RequestDevice device;
}
