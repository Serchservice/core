package com.serch.server.services.transaction.requests;

import com.serch.server.enums.transaction.TransactionType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Getter
@Setter
public class PayRequest {
    private UUID sender;
    private UUID receiver;
    private TransactionType type;
    private BigDecimal amount;
    private String event;
}