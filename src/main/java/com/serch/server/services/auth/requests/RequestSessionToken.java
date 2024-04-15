package com.serch.server.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class RequestSessionToken {
    private String role;
    private UUID id;

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("session_id")
    private UUID sessionId;

    @JsonProperty("refresh_id")
    private UUID refreshId;
}