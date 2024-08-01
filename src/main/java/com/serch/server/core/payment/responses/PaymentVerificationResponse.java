package com.serch.server.core.payment.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentVerificationResponse extends PaymentResponse {
    private PaymentVerificationData data;
}

