package com.serch.server.admin.services.scopes.payment.responses.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class TransactionGroupScopeResponse {
    private String label;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;
    private List<TransactionScopeResponse> transactions;
}