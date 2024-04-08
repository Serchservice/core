package com.serch.server.services.subscription.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubscriptionCardResponse {
    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("account_name")
    private String accountName;

    @JsonProperty("exp_date")
    private String expDate;

    private String bank;
    private String card;
}
