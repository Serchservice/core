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

        return this;
    }

    public static PaymentChargeRequest build(String email, String amount, String authorizationCode) {
        PaymentChargeRequest request = new PaymentChargeRequest();
        request.setEmail(email);
        request.setAmount(amount);
        request.setAuthorizationCode(authorizationCode);

        return request;
    }
}
