package com.serch.server.services.transaction.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import lombok.Data;

@Data
public class TransactionResponse {
    private String id;
    private String name;
    private String avatar;
    private String amount;
    private String time;
    private TransactionStatus status;
    private String reference;
    private Integer size;
    private TransactionType type;
    private String reason;

    @JsonProperty("is_incoming")
    private Boolean isIncoming;

    @JsonProperty("requested_at")
    private String requestedAt;

    @JsonProperty("completed_at")
    private String completedAt;
}
