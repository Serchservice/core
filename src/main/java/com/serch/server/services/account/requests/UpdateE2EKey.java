package com.serch.server.services.account.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateE2EKey {
    @JsonProperty("public_key")
    private String publicKey;
}