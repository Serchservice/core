package com.serch.server.admin.services.scopes.payment.responses.transactions;

import com.serch.server.enums.transaction.TransactionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionTypeResponse {
    private String name;
    private TransactionType type;
}