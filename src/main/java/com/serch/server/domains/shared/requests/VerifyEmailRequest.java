package com.serch.server.domains.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VerifyEmailRequest {
    private String token;
    private String link;
    private String country;
    private String state;

    @JsonProperty("link_id")
    private String linkId;

    @JsonProperty("email_address")
    private String emailAddress;
}
