package com.serch.server.services.payment.core;

import com.serch.server.services.payment.requests.InitializePaymentRequest;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.payment.responses.PaymentVerificationResponse;

public interface PaymentService {
    InitializePaymentData initialize(InitializePaymentRequest request);
    PaymentVerificationResponse verify(String reference);
}