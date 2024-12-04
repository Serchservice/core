package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AccountUserScopeCertificateResponse {
    private String id;
    private Boolean isGenerated;
    private String document;
    private String code;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
}