package com.serch.server.admin.services.scopes.payment.services;

import com.serch.server.admin.services.scopes.payment.responses.payouts.PayoutResponse;
import com.serch.server.admin.services.scopes.payment.responses.transactions.PaymentApiResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.admin.services.scopes.payment.responses.payouts.PayoutScopeResponse;
import com.serch.server.enums.transaction.TransactionStatus;

import java.util.List;

/**
 * Interface defining operations related to payout management.
 * <p>
 * This interface provides methods for managing payout operations, including
 * retrieving, initiating, and canceling payouts. It supports paginated access
 * to large datasets and offers filtering based on payout IDs and statuses,
 * allowing for comprehensive management of individual and grouped payout operations.
 * </p>
 */
public interface PayoutScopeService {

    /**
     * Retrieves a paginated list of payouts, optionally filtered by status.
     * <p>
     * This method supports pagination to handle large sets of payouts and allows
     * filtering based on {@link TransactionStatus}. Each page contains a list of
     * {@link PayoutScopeResponse} objects representing grouped payout details.
     * </p>
     *
     * @param page The page number to retrieve. Must be a non-negative integer, with 0 as the default.
     * @param size The number of items per page. Must be a positive integer.
     * @param status Optional filter for the transaction status. If null, all payouts are included.
     * @return An {@link ApiResponse} containing a {@link PaymentApiResponse} with a list of
     *         {@link PayoutScopeResponse} for the specified page and size. If no payouts are found,
     *         the list will be empty.
     */
    ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>> payouts(Integer page, Integer size, TransactionStatus status);

    /**
     * Retrieves detailed information about a specific payout identified by its unique ID.
     * <p>
     * This method fetches comprehensive details for the payout corresponding to the given ID.
     * </p>
     *
     * @param id The unique identifier of the payout. Must correspond to an existing payout.
     * @return An {@link ApiResponse} containing a {@link PayoutResponse} with detailed information
     *         about the payout. If the payout is not found, the response indicates the error.
     */
    ApiResponse<PayoutResponse> find(String id);

    /**
     * Cancels a specified payout by its unique ID.
     * <p>
     * This method allows canceling payouts that are not yet processed. The cancellation will
     * update the status of the payout, and the updated details are returned in the response.
     * </p>
     *
     * @param id The unique identifier of the payout to cancel.
     * @return An {@link ApiResponse} containing a {@link PayoutResponse} with details of the canceled
     *         payout. If the payout cannot be canceled (e.g., already processed), an error is returned.
     */
    ApiResponse<PayoutResponse> cancel(String id);

    /**
     * Initiates the payout process for the specified payout ID.
     * <p>
     * Triggers the disbursement of funds for the given payout. If the payout is already processed,
     * the current status is returned without reprocessing.
     * </p>
     *
     * @param id The unique identifier of the payout to initiate.
     * @return An {@link ApiResponse} containing a {@link PayoutResponse} with details of the initiated payout.
     *         If the initiation fails, an error is included in the response.
     */
    ApiResponse<PayoutResponse> payout(String id);

    /**
     * Retrieves a paginated list of payouts filtered by a list of payout IDs.
     * <p>
     * This method supports both pagination and filtering based on the provided list of payout IDs.
     * </p>
     *
     * @param page The page number to retrieve. Must be a non-negative integer.
     * @param size The number of items per page. Must be a positive integer.
     * @param id   List of payout IDs to filter the results. If the list is empty, all payouts are retrieved.
     * @return An {@link ApiResponse} containing a {@link PaymentApiResponse} with a list of
     *         {@link PayoutScopeResponse} filtered by the provided payout IDs.
     */
    ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>> payout(Integer page, Integer size, List<String> id);

    /**
     * Retrieves a paginated list of canceled payouts filtered by a list of IDs.
     * <p>
     * Allows for retrieval of payouts that have been canceled, based on the provided list of IDs.
     * Supports pagination for handling large numbers of canceled payouts.
     * </p>
     *
     * @param page The page number to retrieve. Must be a non-negative integer.
     * @param size The number of items per page. Must be a positive integer.
     * @param id   List of payout IDs for filtering. If the list is empty, all canceled payouts are considered.
     * @return An {@link ApiResponse} containing a {@link PaymentApiResponse} with a list of
     *         {@link PayoutScopeResponse} representing the filtered canceled payouts.
     */
    ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>> cancel(Integer page, Integer size, List<String> id);
}