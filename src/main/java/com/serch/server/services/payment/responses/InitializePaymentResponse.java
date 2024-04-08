package com.serch.server.services.payment.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InitializePaymentResponse extends PaymentResponse {
    private InitializePaymentData data;
}