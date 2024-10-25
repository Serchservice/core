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
 * <p>
 * This interface provides functionalities for initializing payment transactions, verifying payments,
 * charging payment authorizations, retrieving bank information, and verifying bank account details.
 * Implementations of this interface should handle the underlying payment processing logic,
 * including communication with payment gateways and bank services.
 * </p>
 *
 * @see Payment
 */
public interface PaymentService {

    /**
     * Initializes a payment transaction based on the provided request data.
     * <p>
     * This method sets up the necessary information for a payment transaction and returns
     * the initialized payment data, which can be used to proceed with the payment process.
     * </p>
     *
     * @param request The request object containing payment initialization data, including
     *                the amount, currency, and payment method details.
     * @return An object representing the initialized payment data, including the transaction
     *         reference and any other relevant information needed to complete the payment.
     *
     * @see InitializePaymentData
     * @see InitializePaymentRequest
     */
    InitializePaymentData initialize(InitializePaymentRequest request);

    /**
     * Verifies the status of a payment transaction using the provided reference.
     * <p>
     * This method checks the current status of a payment transaction by its reference code,
     * allowing the caller to determine whether the payment has been successful, pending, or failed.
     * </p>
     *
     * @param reference The reference code identifying the payment transaction, typically provided
     *                  during the payment initialization process.
     * @return An object representing the payment verification data, which includes the status
     *         of the payment, the amount, and any associated details.
     *
     * @see PaymentVerificationData
     */
    PaymentVerificationData verify(String reference);

    /**
     * Charges a payment authorization based on the provided request data.
     * <p>
     * This method processes a payment charge using the previously authorized payment details,
     * returning the result of the charge operation.
     * </p>
     *
     * @param request The request object containing payment charge data, including the reference
     *                from the initial payment authorization and the amount to charge.
     * @return An object representing the payment verification data, which includes the result
     *         of the charge, such as confirmation of success or failure, and any relevant details.
     *
     * @see PaymentVerificationData
     * @see PaymentChargeRequest
     */
    PaymentVerificationData charge(PaymentChargeRequest request);

    /**
     * Retrieves the list of banks available for payment processing.
     * <p>
     * This method provides a list of banks that can be used for transactions, including their
     * details such as name, code, and supported services.
     * </p>
     *
     * @return A list of {@link Bank} objects representing the banks available for payment transactions.
     */
    List<Bank> banks();

    /**
     * Verifies a bank account to provide ownership details.
     * <p>
     * This method checks the validity of a bank account number using the provided NUBAN code,
     * returning information about the account holder if the verification is successful.
     * </p>
     *
     * @param accountNumber The bank account number to be verified.
     * @param code The bank NUBAN code used for the verification process.
     * @return {@link BankAccount} details, including the account holder's name and other relevant information.
     */
    BankAccount verify(String accountNumber, String code);
}