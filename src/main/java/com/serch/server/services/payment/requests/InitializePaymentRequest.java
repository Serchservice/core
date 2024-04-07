package com.serch.server.services.payment.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;

@Getter
@Setter
@Data
public class InitializePaymentRequest {
    private String email;
    private String amount;
    private String reference;
    private String bearer;
    private ArrayList<String> channels = new ArrayList<>(Collections.singletonList("card"));

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("subaccount")
    private String subAccount;

    @JsonProperty("split_code")
    private String splitCode;

    public InitializePaymentRequest validate() {
        int updatedAmount = Integer.parseInt(getAmount());
        setAmount(String.valueOf(updatedAmount * 100));

        System.out.println(this);
        return this;
    }
}
