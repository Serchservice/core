package com.serch.server.services.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.transaction.requests.FundRequest;
import com.serch.server.services.transaction.requests.PayRequest;
import com.serch.server.services.transaction.requests.WalletUpdateRequest;
import com.serch.server.services.transaction.requests.WithdrawRequest;
import com.serch.server.services.transaction.responses.WalletResponse;

/**
 * This is the wrapper class for WalletImplementation.
 * @see com.serch.server.services.transaction.services.implementations.WalletImplementation
 */
public interface WalletService {
    /**
     * Creates a wallet for the specified user if it doesn't exist already.
     *
     * @param user The user for whom the wallet needs to be created.
     */
    void createWallet(User user);

    /**
     * Removes any saved record of the user
     *
     * @param user The user whose account is to be removed
     */
    void undo(User user);

    /**
     * Initiates a payment based on the provided request.
     *
     * @param request The payment request details.
     * @return An ApiResponse containing the result of the payment initiation.
     */
    ApiResponse<String> pay(PayRequest request);

//    /**
//     * Processes a payment for a trip using the wallet balance.
//     *
//     * @param request The payment request for the trip.
//     * @return An ApiResponse containing the result of the payment.
//     */
//    ApiResponse<String> paySubscription(PayRequest request);

    /**
     * Processes a payment for a subscription using the wallet balance.
     *
     * @param request The payment request for the subscription.
     * @return An ApiResponse containing the result of the payment.
     */
    ApiResponse<String> payTrip(PayRequest request);

    /**
     * Checks if a user can pay for a trip using the wallet balance.
     *
     * @param trip The trip for which the payment is being checked.
     * @return An ApiResponse indicating if the user can pay for the trip with the wallet balance.
     */
    ApiResponse<String> checkIfUserCanPayForTripWithWallet(String trip);

    /**
     * Initiates the funding of a wallet with the specified amount.
     *
     * @param request The fund request containing the amount to be funded.
     * @return An ApiResponse containing the payment initialization data.
     */
    ApiResponse<InitializePaymentData> fundWallet(FundRequest request);

    /**
     * Verifies a fund transaction using the reference provided.
     *
     * @param reference The reference of the fund transaction to be verified.
     * @return An ApiResponse indicating the result of the verification.
     */
    ApiResponse<String> verifyFund(String reference);

    /**
     * Requests a withdrawal of funds from the wallet.
     *
     * @param request The withdrawal request details.
     * @return An ApiResponse containing the result of the withdrawal request.
     */
    ApiResponse<String> requestWithdraw(WithdrawRequest request);

    /**
     * Retrieves details of the user's wallet.
     *
     * @return An ApiResponse containing the wallet details.
     */
    ApiResponse<WalletResponse> view();

    /**
     * Updates the wallet details based on the provided request.
     *
     * @param request The request containing the updated wallet information.
     * @return An ApiResponse indicating the result of the wallet update operation.
     */
    ApiResponse<String> update(WalletUpdateRequest request);
}
