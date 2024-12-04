package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.transaction.responses.WalletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountUserScopeWalletResponse extends WalletResponse {
    private String id;

    @JsonProperty("last_payday")
    private LocalDate lastPayday;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
}