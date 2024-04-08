package com.serch.server.services.payment.core;

import com.serch.server.services.payment.requests.InitializePaymentRequest;
import com.serch.server.services.payment.responses.InitializePaymentResponseData;
import com.serch.server.services.payment.responses.PaymentVerificationResponseData;

public interface PaymentService {
    InitializePaymentResponseData initialize(InitializePaymentRequest request);
    PaymentVerificationResponseData verify(String reference);
}