package com.serch.server.services.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.responses.Bank;
import com.serch.server.core.payment.responses.BankAccount;

import java.util.List;

public interface BankService {
    /**
     * Get the list of banks
     *
     * @return {@link ApiResponse} List of {@link Bank}
     */
    ApiResponse<List<Bank>> banks();

    /**
     * Verifies a bank account to provide owner
     *
     * @param accountNumber The bank account number
     * @param code The bank NUBAN code
     *
     * @return {@link ApiResponse} of {@link BankAccount} details
     */
    ApiResponse<BankAccount> verify(String accountNumber, String code);
}
