package com.serch.server.admin.services.scopes.payment.responses.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class TransactionScopeResponse {
    private String id;
    private TransactionType type;
    private String label;
    private String mode;
    private String amount;
    private String reason;
    private String reference;
    private Long resolution;
    private String event;
    private Boolean verified;
    private TransactionStatus status;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    private CommonProfileResponse sender;
    private CommonProfileResponse recipient;
}