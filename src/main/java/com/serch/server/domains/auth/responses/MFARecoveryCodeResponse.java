package com.serch.server.domains.auth.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MFARecoveryCodeResponse {
    private String code;

    @JsonProperty("is_used")
    private Boolean isUsed;
}