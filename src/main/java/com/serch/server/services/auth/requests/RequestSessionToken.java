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

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("serch_id")
    private UUID serchId;

    @JsonProperty("session_id")
    private UUID sessionId;

    @JsonProperty("refresh_id")
    private UUID refreshId;
}
