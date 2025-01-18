package com.serch.server.domains.transaction.requests;

import lombok.Data;

@Data
public class WithdrawRequest {
    private Integer amount;
}
