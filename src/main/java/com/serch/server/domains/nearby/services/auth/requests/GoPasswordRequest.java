package com.serch.server.domains.nearby.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoPasswordRequest {
    @JsonProperty("old_password")
    private String oldPassword;

    @JsonProperty("new_password")
    private String newPassword;
}