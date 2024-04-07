package com.serch.server.services.payment.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentAuthorization {
    @JsonProperty("authorization_code")
    private String authorizationCode;

    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("exp_month")
    private String expMonth;

    @JsonProperty("exp_year")
    private String expYear;

    private String last4;
    private String bin;
    private String bank;
    private String channel;
    private String signature;
    private Boolean reusable;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("account_name")
    private String accountName;
}
