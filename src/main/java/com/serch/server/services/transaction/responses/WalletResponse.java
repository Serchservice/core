package com.serch.server.services.transaction.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletResponse {
    private String accountName;
    private String accountNumber;
    private String deposit;
    private String bankName;
    private String balance;
}
