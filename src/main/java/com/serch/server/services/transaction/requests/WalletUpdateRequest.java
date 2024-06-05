package com.serch.server.services.transaction.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WalletUpdateRequest {
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("account_name")
    private String accountName;
    @JsonProperty("bank_name")
    private String bankName;

    private Integer payday;
    private Integer payout;

    @JsonProperty("payout_on_payday")
    private Boolean payoutOnPayday;
}
