package com.serch.server.services.auth.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PendingRegistrationResponse {
    private String token;
    private String message;

    @JsonProperty("error_code")
    private String errorCode;
}