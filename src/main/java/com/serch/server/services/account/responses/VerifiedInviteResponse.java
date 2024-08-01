package com.serch.server.services.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VerifiedInviteResponse {
    private String name;
    private String scope;

    @JsonProperty("email_address")
    private String emailAddress;
}