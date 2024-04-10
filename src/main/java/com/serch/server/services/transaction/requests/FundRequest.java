package com.serch.server.services.transaction.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FundRequest {
    private Integer amount;

    @JsonProperty("callback_url")
    private String callbackUrl;
}
