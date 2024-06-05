package com.serch.server.services.transaction.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import lombok.Data;

@Data
public class TransactionResponse {
    private String recipient;
    private String amount;
    private String label;
    private TransactionStatus status;
    private TransactionType type;

    @JsonProperty("is_incoming")
    private Boolean isIncoming;

    private TransactionData data;
    private AssociateTransactionData associate;
}