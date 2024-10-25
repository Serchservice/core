package com.serch.server.admin.services.scopes.payment.services;

import com.serch.server.admin.services.scopes.payment.responses.transactions.PaymentApiResponse;
import com.serch.server.admin.services.scopes.payment.responses.wallet.WalletScopeResponse;
import com.serch.server.bases.ApiResponse;

/**
 * Interface defining operations related to wallet scope management.
 * <p>
 * This interface provides methods for managing wallets within the system,
 * including retrieving individual wallet details and accessing collections of wallets.
 * It supports operations for both single wallet information and paginated retrieval
 * of multiple wallets.
 * </p>
 */
public interface WalletScopeService {

    /**
     * Retrieves the details of a specific wallet by its unique wallet ID.
     * <p>
     * This method fetches information about the wallet associated with the given ID.
     * If the wallet does not exist, the response will include an error status
     * indicating the reason for the failure.
     * </p>
     *
     * @param id The unique identifier for the wallet. Must correspond to an existing wallet in the system.
     * @return An {@link ApiResponse} containing a {@link WalletScopeResponse} with wallet details.
     *         If the wallet is not found, the response will indicate the appropriate error status.
     */
    ApiResponse<WalletScopeResponse> wallet(String id);

    /**
     * Retrieves a paginated list of all wallets in the system.
     * <p>
     * Supports pagination to effectively manage large datasets of wallet information.
     * Each page contains a list of {@link WalletScopeResponse} objects representing individual wallets.
     * </p>
     *
     * @param page The page number to retrieve, starting from 0. Must be a non-negative integer.
     *             Allows paginated access to the wallet list.
     * @param size The number of wallets to include per page. Must be a positive integer.
     *             This parameter controls the size of the returned list.
     * @return An {@link ApiResponse} containing a {@link PaymentApiResponse} with a list of
     *         {@link WalletScopeResponse} objects for the specified page and size.
     *         If no wallets are found, the response will indicate an empty list.
     */
    ApiResponse<PaymentApiResponse<WalletScopeResponse>> wallets(Integer page, Integer size);
}