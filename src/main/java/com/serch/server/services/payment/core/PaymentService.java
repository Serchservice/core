package com.serch.server.services.payment.core;

import com.serch.server.services.payment.requests.InitializePaymentRequest;
import com.serch.server.services.payment.requests.PaymentChargeRequest;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.payment.responses.PaymentVerificationData;

public interface PaymentService {
    InitializePaymentData initialize(InitializePaymentRequest request);
    PaymentVerificationData verify(String reference);
    PaymentVerificationData charge(PaymentChargeRequest request);
}