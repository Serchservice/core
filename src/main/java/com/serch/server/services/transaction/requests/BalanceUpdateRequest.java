package com.serch.server.services.transaction.requests;

import com.serch.server.enums.transaction.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class BalanceUpdateRequest {
    private TransactionType type;
    private UUID user;
    private BigDecimal amount;
}
