package com.serch.server.admin.services.scopes.payment.responses.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.services.transaction.responses.WalletResponse;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
public class WalletScopeResponse {
    private CommonProfileResponse profile;
    private WalletResponse wallet;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    @JsonProperty("last_payout_date")
    private LocalDate lastPayoutDate;
}