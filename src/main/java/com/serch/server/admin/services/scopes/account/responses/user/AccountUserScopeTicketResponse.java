package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.enums.company.IssueStatus;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AccountUserScopeTicketResponse {
    private String ticket;
    private IssueStatus status;
    private CommonProfileResponse admin;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
}