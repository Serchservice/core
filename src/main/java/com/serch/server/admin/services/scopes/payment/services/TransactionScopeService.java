package com.serch.server.admin.services.scopes.payment.services;

import com.serch.server.admin.services.scopes.payment.responses.transactions.*;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Service interface for managing transaction-related operations within the payment scope.
 * <p>
 * This interface provides methods for handling various transaction management tasks,
 * including retrieving transactions based on different criteria, resolving transaction statuses,
 * and fetching available transaction types. It supports paginated access to transactions,
 * resolution of transaction statuses, and grouping of transaction records.
 * </p>
 */
public interface TransactionScopeService {

    /**
     * Retrieves a paginated list of transactions grouped by their status.
     * <p>
     * Transactions are categorized based on their {@link TransactionStatus}, with each group
     * containing transactions sharing the same status, organized further by date.
     * </p>
     *
     * @param page   the page number to retrieve, starting from 0 (defaults to 0 if not specified).
     *               Must be a non-negative integer.
     * @param size   the number of transactions per page, with a default value of 20 if not specified.
     *               Must be a positive integer.
     * @return an {@link ApiResponse} containing a {@link PaymentApiResponse} with a list of
     *         {@link TransactionGroupScopeResponse}, where each response represents
     *         a specific status and its associated transactions.
     */
    ApiResponse<List<PaymentApiResponse<TransactionGroupScopeResponse>>> transactions(Integer page, Integer size);

    /**
     * Retrieves a paginated list of transactions for a specific user, wallet or transaction id.
     * <p>
     * This method filters transactions based on the specified user, wallet or transaction identifiers.
     * The transactions are grouped by their {@link TransactionStatus}, with paginated results.
     * </p>
     *
     * @param page   the page number to retrieve, starting from 0 (defaults to 0 if not specified).
     *               Must be a non-negative integer.
     * @param size   the number of transactions per page, with a default value of 20 if not specified.
     *               Must be a positive integer.
     * @param query the query string that can contain user, wallet or transaction id.
     * @return an {@link ApiResponse} containing a {@link PaymentApiResponse} with a list of
     *         {@link TransactionGroupScopeResponse} objects representing the filtered transactions.
     */
    ApiResponse<List<PaymentApiResponse<TransactionGroupScopeResponse>>> search(Integer page, Integer size, String query);

    /**
     * Retrieves a paginated list of transactions for a specific filter type or date range.
     * <p>
     * This method filters transactions based on the specified user, wallet or transaction identifiers.
     * The transactions are grouped by their {@link TransactionStatus}, with paginated results.
     * </p>
     *
     * @param start   the start range for the transactions to be fetched.
     * @param end   the end range for the transactions to be fetched.
     * @param type the filter {@link TransactionType} to use.
     * @param page   the page number to retrieve, starting from 0 (defaults to 0 if not specified).
     *               Must be a non-negative integer.
     * @param size   the number of transactions per page, with a default value of 20 if not specified.
     *               Must be a positive integer.
     * @param status the {@link TransactionStatus} to filter transactions by. If null, transactions
     *               from all statuses will be considered.
     * @return an {@link ApiResponse} containing a {@link PaymentApiResponse} with a list of
     *         {@link TransactionGroupScopeResponse} objects representing the filtered transactions.
     */
    ApiResponse<PaymentApiResponse<TransactionGroupScopeResponse>> filter(
            Integer page, Integer size, TransactionType type,
            ZonedDateTime start, ZonedDateTime end, TransactionStatus status
    );

    /**
     * Fetches the list of all available transaction types.
     * <p>
     * Transaction types categorize different transaction activities within the system.
     * This method provides the supported transaction types.
     * </p>
     *
     * @return an {@link ApiResponse} containing a list of {@link TransactionTypeResponse},
     *         with each response providing details about a specific type of transaction.
     */
    ApiResponse<List<TransactionTypeResponse>> fetchTypes();

    /**
     * Resolves a transaction by updating its status.
     * <p>
     * This method changes the state of a specified transaction, identified by its ID,
     * to a new {@link TransactionStatus}. Additional business logic may be executed
     * to process or settle the transaction.
     * </p>
     *
     * @param id     the unique identifier of the transaction to resolve.
     * @param status the new {@link TransactionStatus} to assign to the transaction.
     * @return an {@link ApiResponse} containing the updated {@link TransactionScopeResponse},
     *         which includes the latest details of the resolved transaction.
     */
    ApiResponse<TransactionScopeResponse> resolve(String id, TransactionStatus status);

    /**
     * Retrieves details of a specific transaction identified by its unique ID.
     * <p>
     * This method returns comprehensive information about the transaction, such as its status,
     * amount, date, and associated metadata, enabling users to track and view their financial
     * activities.
     * </p>
     *
     * @param id the unique identifier of the transaction to retrieve.
     *           If the transaction is not found, an error response indicating the issue will be returned.
     * @return an {@link ApiResponse} containing a {@link TransactionScopeResponse} with the transaction details.
     */
    ApiResponse<TransactionScopeResponse> transaction(String id);

    /**
     * Retrieves a paginated list of resolved transactions.
     * <p>
     * This method returns a paginated collection of transactions that have been processed
     * or settled based on the application's business rules.
     * </p>
     *
     * @param page the page number to retrieve, starting from 0 (defaults to 0 if not specified).
     *             Must be a non-negative integer.
     * @param size the number of items per page, with a default value of 20 if not specified.
     *             Must be a positive integer.
     * @return an {@link ApiResponse} containing a {@link PaymentApiResponse} with a list of
     *         {@link ResolvedTransactionResponse}, representing groups of resolved transactions.
     */
    ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>> resolvedTransactions(Integer page, Integer size);

    /**
     * Retrieves a paginated list of resolved transactions grouped by specified criteria.
     * <p>
     * This method returns a paginated collection of transactions that have been processed
     * or settled based on the application's business rules.
     * </p>
     *
     * @param query the query that can contain user id, wallet id, transaction id, etc.
     * @param page the page number to retrieve, starting from 0 (defaults to 0 if not specified).
     *             Must be a non-negative integer.
     * @param size the number of items per page, with a default value of 20 if not specified.
     *             Must be a positive integer.
     * @return an {@link ApiResponse} containing a {@link PaymentApiResponse} with a list of
     *         {@link ResolvedTransactionResponse}, representing groups of resolved transactions.
     */
    ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>> searchResolved(Integer page, Integer size, String query);

    /**
     * Retrieves a paginated list of resolved transactions grouped by date range.
     * <p>
     * This method returns a paginated collection of transactions that have been processed
     * or settled based on the application's business rules.
     * </p>
     *
     * @param start   the start range for the transactions to be fetched.
     * @param end   the end range for the transactions to be fetched.
     * @param page the page number to retrieve, starting from 0 (defaults to 0 if not specified).
     *             Must be a non-negative integer.
     * @param type the filter {@link TransactionType} to use.
     * @param status the {@link TransactionStatus} to filter transactions by. If null, transactions
     *               from all statuses will be considered.
     * @param size the number of items per page, with a default value of 20 if not specified.
     *             Must be a positive integer.
     * @return an {@link ApiResponse} containing a {@link PaymentApiResponse} with a list of
     *         {@link ResolvedTransactionResponse}, representing groups of resolved transactions.
     */
    ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>> filterResolved(
            Integer page, Integer size, ZonedDateTime start, ZonedDateTime end,
            TransactionType type, TransactionStatus status
    );

    /**
     * Retrieves a resolved transactions by id.
     * <p>
     * This method returns the transaction that have been processed
     * or settled based on the application's business rules.
     * </p>
     *
     * @param id the Id of the resolved transaction.
     * @return an {@link ApiResponse} containing a {@link ResolvedTransactionResponse},
     * representing a resolved transaction.
     */
    ApiResponse<ResolvedTransactionResponse> resolved(Long id);
}