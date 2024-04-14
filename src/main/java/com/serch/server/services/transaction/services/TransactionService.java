package com.serch.server.services.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.transaction.responses.TransactionResponse;

import java.util.List;

/**
 * This is the wrapper class for the Transaction implementation
 * @see com.serch.server.services.transaction.services.implementations.TransactionImplementation
 */
public interface TransactionService {
    /**
     * Retrieves a list of transactions for the current user.
     *
     * @return ApiResponse containing the list of transactions.
     */
    ApiResponse<List<TransactionResponse>> transactions();
}
