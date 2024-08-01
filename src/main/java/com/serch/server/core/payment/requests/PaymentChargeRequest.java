package com.serch.server.core.payment.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PaymentChargeRequest {
    private String email;
    private String amount;

    @JsonProperty("authorization_code")
    private String authorizationCode;

    public PaymentChargeRequest validate() {
        int updatedAmount = Integer.parseInt(getAmount());
        setAmount(String.valueOf(updatedAmount * 100));

        System.out.println("PaymentChargeRequest - " + this);
        return this;
    }
}
