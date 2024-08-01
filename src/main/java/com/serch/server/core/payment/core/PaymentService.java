package com.serch.server.core.payment.core;

import com.serch.server.core.payment.requests.InitializePaymentRequest;
import com.serch.server.core.payment.requests.PaymentChargeRequest;
import com.serch.server.core.payment.responses.Bank;
import com.serch.server.core.payment.responses.BankAccount;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.core.payment.responses.PaymentVerificationData;

import java.util.List;

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

    /**
     * Get the list of banks
     *
     * @return List of {@link Bank}
     */
    List<Bank> banks();

    /**
     * Verifies a bank account to provide owner
     *
     * @param accountNumber The bank account number
     * @param code The bank NUBAN code
     *
     * @return {@link BankAccount} details
     */
    BankAccount verify(String accountNumber, String code);
}