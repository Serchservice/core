package com.serch.server.core.payment.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
@Data
public class InitializePaymentRequest {
    private String email;
    private String amount;
    private String reference;
    private String bearer;
    private ArrayList<String> channels = new ArrayList<>(Arrays.asList("card", "ussd", "bank", "qr", "mobile_money"));

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("subaccount")
    private String subAccount;

    @JsonProperty("split_code")
    private String splitCode;

    @JsonIgnore
    public InitializePaymentRequest validate() {
        int updatedAmount = Integer.parseInt(getAmount());
        setAmount(String.valueOf(updatedAmount * 100));

        System.out.println("InitializePaymentRequest - " + this);
        return this;
    }
}
