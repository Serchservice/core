package com.serch.server.services.payment.core;

import com.serch.server.services.payment.requests.InitializePaymentRequest;
import com.serch.server.services.payment.requests.PaymentChargeRequest;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.payment.responses.PaymentVerificationData;

/**
 * Interface defining methods for handling payment-related operations.
 *
 * @see Payment
 */
public interface PaymentService {

    /**
     * Initializes a payment transaction based on the provided request data.
     *
     * @param request The request object containing payment initialization data.
     * @return An object representing the initialized payment data.
     *
     * @see InitializePaymentData
     * @see InitializePaymentRequest
     */
    InitializePaymentData initialize(InitializePaymentRequest request);

    /**
     * Verifies the status of a payment transaction using the provided reference.
     *
     * @param reference The reference code identifying the payment transaction.
     * @return An object representing the payment verification data.
     *
     * @see PaymentVerificationData
     */
    PaymentVerificationData verify(String reference);

    /**
     * Charges a payment authorization based on the provided request data.
     *
     * @param request The request object containing payment charge data.
     * @return An object representing the payment verification data.
     *
     * @see PaymentVerificationData
     * @see PaymentChargeRequest
     */
    PaymentVerificationData charge(PaymentChargeRequest request);
}