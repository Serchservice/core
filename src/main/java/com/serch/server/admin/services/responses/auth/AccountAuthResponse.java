package com.serch.server.admin.services.responses.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountAuthResponse {
    @JsonProperty("has_mfa")
    private Boolean hasMFA;

    @JsonProperty("must_have_mfa")
    private Boolean mustHaveMFA;

    private String method;
    private String level;
}