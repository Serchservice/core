package com.serch.server.domains.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.transaction.responses.TransactionGroupResponse;
import com.serch.server.domains.transaction.responses.TransactionResponse;

import java.util.List;

public interface TransactionResponseService {
    /**
     * Fetches the list of transactions for the logged-in user.
     *<p>
     * This method retrieves all transactions associated with the user's wallet,
     * providing a comprehensive overview of their financial activity. The response
     * includes details about each transaction, such as amounts, dates, and types.
     *
     * @return An ApiResponse containing a list of TransactionGroupResponse objects.
     */
    ApiResponse<List<TransactionGroupResponse>> transactions(Integer page, Integer size);

    /**
     * Fetches the recent transactions of the logged-in user.
     * <p>
     * This method combines all different transaction types and limits the result to
     * the most recent five transactions. It allows users to quickly view their latest
     * financial activities.
     *
     * @return An ApiResponse containing a list of TransactionGroupResponse objects.
     */
    ApiResponse<List<TransactionGroupResponse>> recent();


    /**
     * Prepare the readable transaction details of a transaction
     *
     * @param id The transaction id to look for
     *
     * @return {@link TransactionResponse} data
     */
    TransactionResponse response(String id);
}
