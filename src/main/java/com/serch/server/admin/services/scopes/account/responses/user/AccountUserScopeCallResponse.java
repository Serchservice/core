package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.call.CallType;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AccountUserScopeCallResponse {
    private String channel;
    private CallType type;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
}