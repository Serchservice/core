package com.serch.server.services.transaction.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import lombok.Data;

@Data
public class RecentTransaction {
    private String id;
    private String name;
    private String amount;
    private String time;
    private TransactionStatus status;
    private TransactionType type;
    private String mode;

    @JsonProperty("is_incoming")
    private Boolean isIncoming;

    @JsonProperty("requested_at")
    private String requestedAt;

    @JsonProperty("completed_at")
    private String completedAt;
}
