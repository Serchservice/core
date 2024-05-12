package com.serch.server.services.transaction.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.services.shared.responses.SharedStatusData;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TransactionResponse {
    private String id;
    private String name;
    private String amount;
    private String time;
    private TransactionStatus status;
    private String reference;
    private Integer size;
    private TransactionType type;
    private String reason;
    private String mode;
    private List<AssociateTransactionData> associates;
    private SharedStatusData pricing;

    @JsonProperty("is_incoming")
    private Boolean isIncoming;

    @JsonProperty("requested_at")
    private String requestedAt;

    @JsonProperty("completed_at")
    private String completedAt;

    @JsonIgnore
    private LocalDateTime createdAt;
}
