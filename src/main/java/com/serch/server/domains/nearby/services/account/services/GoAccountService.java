package com.serch.server.domains.nearby.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.account.requests.GoAccountUpdateRequest;
import com.serch.server.domains.nearby.services.account.responses.GoAccountResponse;

/**
 * This interface defines the contract for account management operations.
 * It provides methods for updating account information and retrieving account details.
 */
public interface GoAccountService {
    /**
     * Updates the user's account information.
     *
     * @param request The {@link GoAccountUpdateRequest} account update request containing the new information.
     * @return An {@link ApiResponse} containing {@link GoAccountResponse} the updated account details, or an error message.
     */
    ApiResponse<GoAccountResponse> update(GoAccountUpdateRequest request);

    /**
     * Retrieves the current user's account details.
     *
     * @return An {@link ApiResponse} containing {@link GoAccountResponse} the account details, or an error message.
     */
    ApiResponse<GoAccountResponse> get();
}