package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.shared.UseStatus;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AccountUserScopeSharedLinkResponse {
    private String id;
    private String link;
    private String amount;
    private UseStatus status;

    @JsonProperty("is_expired")
    private Boolean isExpired;

    @JsonProperty("cannot_login")
    private Boolean cannotLogin;

    @JsonProperty("next_status")
    private UseStatus nextStatus;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
}
