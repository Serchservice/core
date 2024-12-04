package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.enums.EventType;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class AccountUserScopeTransactionResponse {
    private String id;
    private BigDecimal amount;
    private String reference;
    private String account;
    private String mode;
    private String event;
    private Boolean verified;
    private TransactionStatus status;
    private TransactionType type;
    private String reason;
    private CommonProfileResponse sender;

    @JsonProperty("event_type")
    private EventType eventType;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
}
