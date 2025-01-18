package com.serch.server.domains.transaction.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
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

    @JsonProperty("next_payday")
    private String nextPayday;

    @JsonProperty("payout_on_payday")
    private Boolean payoutOnPayday;
}
