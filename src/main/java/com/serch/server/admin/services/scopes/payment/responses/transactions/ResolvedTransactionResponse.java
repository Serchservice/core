package com.serch.server.admin.services.scopes.payment.responses.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.enums.transaction.TransactionStatus;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ResolvedTransactionResponse {
    private Long id;
    private TransactionScopeResponse transaction;
    private TransactionStatus status;
    private CommonProfileResponse admin;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
}