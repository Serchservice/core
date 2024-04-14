package com.serch.server.services.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.responses.SharedAccountResponse;

import java.util.List;

/**
 * Service interface for shared operations.
 *
 * @see com.serch.server.services.shared.services.implementations.SharedImplementation
 */
public interface SharedService {
    /**
     * Retrieves account information based on the provided ID.
     *
     * @param id The ID of the account.
     * @return A response containing the account information.
     */
    ApiResponse<AccountResponse> accounts(String id);

    /**
     * Retrieves a list of guest responses.
     *
     * @return A response containing a list of guest responses.
     */
    ApiResponse<List<GuestResponse>> links();
}

