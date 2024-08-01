package com.serch.server.admin.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AdminLoginRequest {
    @JsonProperty(value = "email_address")
    private String emailAddress;

    private String password;
}