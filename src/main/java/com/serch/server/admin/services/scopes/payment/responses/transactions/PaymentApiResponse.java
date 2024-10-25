package com.serch.server.admin.services.scopes.payment.responses.transactions;

import com.serch.server.enums.transaction.TransactionStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentApiResponse<T> {
    private TransactionStatus status;
    private String title;
    private Long total = 0L;

    private List<T> transactions = new ArrayList<>();
}