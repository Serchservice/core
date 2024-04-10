package com.serch.server.services.transaction.requests;

import lombok.Data;

@Data
public class WithdrawRequest {
    private Integer amount;
}
