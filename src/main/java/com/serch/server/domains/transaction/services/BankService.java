package com.serch.server.domains.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.responses.Bank;
import com.serch.server.core.payment.responses.BankAccount;

import java.util.List;

/**
 * BankService interface defines the operations related to banking services,
 * including retrieving bank information and verifying bank account details.
 */
public interface BankService {

    /**
     * Retrieves a list of available banks.
     * <p>
     * This method queries the underlying data source to obtain a comprehensive
     * list of banks. The response includes essential information about each bank,
     * such as its name, NUBAN code, and other relevant details.
     * </p>
     *
     * @return {@link ApiResponse} containing a list of {@link Bank} objects,
     *         each representing an available bank.
     */
    ApiResponse<List<Bank>> banks();

    /**
     * Verifies a bank account to confirm its ownership.
     * <p>
     * This method checks the provided bank account number against the specified
     * NUBAN code to ensure that the account is valid and belongs to the expected
     * owner. This is typically used in scenarios such as setting up payments
     * or verifying transactions.
     * </p>
     *
     * @param accountNumber The bank account number to verify. This should be a
     *                      valid account number associated with a specific bank.
     * @param code The NUBAN code of the bank associated with the account. This
     *             code is necessary for the verification process to identify the
     *             correct bank.
     *
     * @return {@link ApiResponse} containing the details of the {@link BankAccount},
     *         including information such as the account holder's name and account status.
     */
    ApiResponse<BankAccount> verify(String accountNumber, String code);
}