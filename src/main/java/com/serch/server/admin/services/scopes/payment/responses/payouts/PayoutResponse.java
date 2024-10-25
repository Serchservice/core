package com.serch.server.admin.services.scopes.payment.responses.payouts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class PayoutResponse {
    private Long id;
    private CommonProfileResponse admin;
    private CommonProfileResponse user;
    private TransactionStatus status;
    private TransactionType type;
    private String transaction;
    private String event;
    private String amount;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    @JsonProperty("bank_name")
    private String bankName;

    @JsonProperty("account_name")
    private String accountName;

    @JsonProperty("account_number")
    private String accountNumber;
}