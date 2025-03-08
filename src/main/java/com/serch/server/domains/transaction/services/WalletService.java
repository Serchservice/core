package com.serch.server.domains.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.domains.transaction.requests.FundWalletRequest;
import com.serch.server.domains.transaction.requests.WalletUpdateRequest;
import com.serch.server.domains.transaction.requests.WithdrawRequest;
import com.serch.server.domains.transaction.responses.WalletResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.transaction.Wallet;

import java.util.UUID;

/**
 * WalletService interface defines operations related to managing user wallets,
 * including funding, withdrawing, and viewing wallet details, as well as processing
 * transactions related to wallet activities.
 *<p>
 * This service acts as a wrapper for the WalletImplementation, providing a high-level
 * interface for wallet operations while abstracting the underlying implementation details.
 *
 * @see com.serch.server.domains.transaction.services.implementations.WalletImplementation
 */
public interface WalletService {

    /**
     * Creates a wallet for the specified user if it doesn't exist already.
     *<p>
     * This method is used to initialize a wallet for a new user, allowing them
     * to start managing funds. If the user already has a wallet, this method will
     * not create a duplicate.
     *
     * @param user The user for whom the wallet needs to be created.
     */
    void create(User user);

    /**
     * Removes any saved record of the user.
     *<p>
     * This method is intended to delete the user's wallet and all associated
     * records. It may be used in cases where a user decides to close their account
     * or when their wallet needs to be reset for any reason.
     *
     * @param user The user whose account is to be removed.
     */
    void undo(User user);

    /**
     * Initiates the funding of a wallet with the specified amount.
     *<p>
     * This method processes a request to fund the user's wallet, typically by
     * charging a payment method associated with the user. It returns an
     * ApiResponse that contains the initialization response needed to complete the payment.
     *
     * @param request The fund request containing the amount to be funded and other details.
     * @return An ApiResponse containing the payment initialization response.
     */
    ApiResponse<InitializePaymentData> fund(FundWalletRequest request);

    /**
     * Verifies a fund transaction using the reference provided.
     *<p>
     * This method checks the status of a fund transaction based on its unique reference.
     * It ensures that the transaction has been processed correctly and updates the wallet
     * status accordingly.
     *
     * @param reference The reference of the fund transaction to be verified.
     * @return An ApiResponse indicating the result of the verification.
     * @see WalletResponse
     */
    ApiResponse<WalletResponse> verify(String reference);

    /**
     * Processes uncleared debts associated with a specified wallet.
     * This method evaluates the debts linked to the provided wallet ID,
     * and applies necessary actions to clear or manage those debts.
     * It may involve operations such as notifications, updates, or
     * adjustments to the wallet's balance based on the uncleared debts.
     *
     * @param id The ID of the wallet for which uncleared debts will be processed.
     *                 This ID must correspond to an existing wallet in the system.
     */
    void processUnclearedDebts(String id);

    /**
     * Requests a withdrawal of funds from the wallet.
     * <p>
     * This method processes a withdrawal request, transferring funds from the user's
     * wallet to a specified account. It returns an ApiResponse indicating the result
     * of the withdrawal operation.
     *
     * @param request The withdrawal request details, including amount and destination.
     * @return An ApiResponse containing the result of the withdrawal request.
     */
    ApiResponse<String> withdraw(WithdrawRequest request);

    /**
     * Retrieves details of the user's wallet.
     *<p>
     * This method returns the current status of the user's wallet, including balance,
     * transaction history, and other relevant information. It provides users with a
     * comprehensive view of their wallet's activity.
     *
     * @return An ApiResponse containing the wallet details.
     * @see WalletResponse
     */
    ApiResponse<WalletResponse> view();

    /**
     * Builds a wallet response object based on the provided wallet entity.
     * The response type is specified by the generic type parameter.
     *
     * @param wallet   The {@link Wallet} entity from which the response is built.
     * @param response The instance of the wallet response to populate.
     * @param <T>      The type of the wallet response, extending {@link WalletResponse}.
     * @return The populated wallet response of type T.
     */
    <T extends WalletResponse> T buildWallet(Wallet wallet, T response);

    /**
     * Updates the wallet details based on the provided request.
     *<p>
     * This method allows modifications to the wallet's configuration, such as
     * updating user preferences or changing linked payment methods. It returns
     * an ApiResponse indicating the success or failure of the update operation.
     *
     * @param request The request containing the updated wallet information.
     * @return An ApiResponse indicating the result of the wallet update operation.
     */
    ApiResponse<String> update(WalletUpdateRequest request);

    /**
     * Processes the payment for Tip2Fix calls at any moment in time.
     *<p>
     * This method handles payment transactions for Tip2Fix calls initiated by users,
     * ensuring that the appropriate amount is charged from the sender's wallet and
     * credited to the receiver's profile. It also manages any related financial operations.
     *
     * @param channel The call channel for the activity, which may determine specific payment rules.
     * @param sender The sender's UUID, identifying the user initiating the payment.
     * @param receiver The receiver's profile, which contains information about the user receiving the payment.
     */
    void processTip2FixCallPayment(String channel, UUID sender, Profile receiver);

    /**
     * Checks if the caller has the given amount required for Tip2Fix calls.
     *<p>
     * This method verifies whether the caller's wallet has sufficient funds to cover the cost
     * of the Tip2Fix calls. If the caller's balance is insufficient, appropriate actions
     * may be taken to prevent the call from proceeding.
     *
     * @param caller The caller's UUID id, used to access their wallet information.
     */
    void checkBalanceForTip2Fix(UUID caller);

    /**
     * Automates the transfer of balance to different user accounts on their specific paydays.
     * <p>
     * This method processes scheduled transfers of funds to users' accounts based on their
     * predefined payday schedules. It ensures that users receive their payments on time without
     * manual intervention.
     */
    void processPaydays();

    /**
     * Formats the next payday for a given wallet into a user-friendly string representation.
     * <p>
     * This method calculates the upcoming payday based on the wallet's configuration or rules,
     * and returns it in a standard date format that is easily readable by users.
     * The formatting may depend on locale settings or predefined formatting rules.
     *</p>
     *
     * @param wallet The {@link Wallet} object containing the necessary details
     *               to determine the next payday. This object must be non-null and
     *               properly initialized.
     * @return A string representing the formatted date of the next payday.
     *         If the next payday cannot be determined, this method may return
     *         a specific message or null, indicating the inability to compute the date.
     */
    String formatNextPayday(Wallet wallet);

    /**
     * Automates the verification of pending transactions.
     *<p>
     * This method routinely checks for transactions that require verification and processes them
     * to ensure that all financial activities are accurately recorded and any discrepancies
     * are addressed promptly.
     */
    void processPendingVerifications();
}