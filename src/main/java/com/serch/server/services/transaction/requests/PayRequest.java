package com.serch.server.services.transaction.requests;

import com.serch.server.enums.transaction.TransactionType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class PayRequest {
    private TransactionType type;
    private BigDecimal amount;
    private String event;
}