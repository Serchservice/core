package com.serch.server.domains.transaction.responses;

import lombok.Data;

import java.util.List;

@Data
public class TransactionGroupResponse {
    private String label;
    private List<TransactionResponse> transactions;
}
