package com.serch.server.services.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.services.transaction.requests.FundWalletRequest;
import com.serch.server.services.transaction.requests.WalletUpdateRequest;
import com.serch.server.services.transaction.requests.WithdrawRequest;
import com.serch.server.services.transaction.responses.TransactionGroupResponse;
import com.serch.server.services.transaction.responses.WalletResponse;

import java.util.List;
import java.util.UUID;

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
    void create(User user);

    /**
     * Removes any saved record of the user
     *
     * @param user The user whose account is to be removed
     */
    void undo(User user);

    /**
     * Initiates the funding of a wallet with the specified amount.
     *
     * @param request The fund request containing the amount to be funded.
     * @return An ApiResponse containing the payment initialization data.
     */
    ApiResponse<InitializePaymentData> fund(FundWalletRequest request);

    /**
     * Verifies a fund transaction using the reference provided.
     *
     * @param reference The reference of the fund transaction to be verified.
     *
     * @return An ApiResponse indicating the result of the verification.
     * @see WalletResponse
     */
    ApiResponse<WalletResponse> verify(String reference);

    /**
     * Requests a withdrawal of funds from the wallet.
     *
     * @param request The withdrawal request details.
     * @return An ApiResponse containing the result of the withdrawal request.
     */
    ApiResponse<String> withdraw(WithdrawRequest request);

    /**
     * Retrieves details of the user's wallet.
     *
     * @return An ApiResponse containing the wallet details.
     * @see WalletResponse
     */
    ApiResponse<WalletResponse> view();

    /**
     * Updates the wallet details based on the provided request.
     *
     * @param request The request containing the updated wallet information.
     * @return An ApiResponse indicating the result of the wallet update operation.
     */
    ApiResponse<String> update(WalletUpdateRequest request);

    /**
     * Fetch the list of transactions for the logged-in user
     *
     * @return {@link ApiResponse} list of {@link TransactionGroupResponse}
     */
    ApiResponse<List<TransactionGroupResponse>> transactions();

    /**
     * Fetch the recent transactions of the logged-in user. Combines all different transaction
     * types and limits the result to 5
     *
     * @return {@link ApiResponse} list of {@link TransactionGroupResponse}
     */
    ApiResponse<List<TransactionGroupResponse>> recent();

    /**
     * This will process the payment for Tip2Fix call at any moment in time
     *
     * @param channel The call channel for the event
     * @param sender The sender's id {The Caller's UUID}
     * @param receiver The receiver's profile
     */
    void processTip2FixCallPayment(String channel, UUID sender, Profile receiver);

    /**
     * This will check if the caller has the given amount required for Tip2Fix calls
     *
     * @param caller The Caller's UUID id
     */
    void checkBalanceForTip2Fix(UUID caller);

    /**
     * This will automate the transfer of balance to different user accounts on their specific paydays
     */
    void processPaydays();

    /**
     * This will automate the verification of pending transactions
     */
    void processPendingVerifications();
}
