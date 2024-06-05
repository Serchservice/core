package com.serch.server.services.transaction.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransactionData {
    private String id;
    private String header;
    private String description;
    private String reference;
    private String mode;
    private String date;

    @JsonProperty("updated_at")
    private String updatedAt;
}
