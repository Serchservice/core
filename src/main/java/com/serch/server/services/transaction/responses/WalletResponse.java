package com.serch.server.services.transaction.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletResponse {
    private String wallet;
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String balance;
    private String deposit;
    private String payout;
    private String uncleared;
    private Integer payday;

    @JsonProperty("payout_on_payday")
    private Boolean payoutOnPayday;
}
