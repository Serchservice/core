package com.serch.server.services.transaction.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.backend.enums.transaction.TransactionStatus;
import com.serch.backend.enums.transaction.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AllTransactionResponse {
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private String id;
    private TransactionStatus status;
    private TransactionType type;
    private String amount;

    @JsonProperty("sender_name")
    private String senderFullName;

    @JsonProperty("receiver_name")
    private String receiverFullName;
}
