package com.serch.server.admin.services.scopes.account.services;

import com.serch.server.admin.services.scopes.account.responses.AccountSectionMetricResponse;
import com.serch.server.admin.services.scopes.account.responses.AccountSectionResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;

/**
 * Service interface for handling platform account section-related operations,
 * such as fetching metrics and paginated account response filtered by criteria.
 */
public interface AccountSectionScopeService {
    /**
     * Fetches metrics based on the given user role.
     *
     * @param role The role of the user requesting the metrics.
     * @return An {@link ApiResponse} containing a {@link AccountSectionMetricResponse} object.
     */
    ApiResponse<AccountSectionMetricResponse> fetchMetric(Role role);

    /**
     * Fetches a paginated list of platform accounts based on the given user role and filter criteria.
     *
     * @param role The role of the user requesting the accounts.
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @param alphabet An optional alphabet character to filter accounts by the starting letter
     *                 of their names (based on the "lastname" field in the database).
     * @return An {@link ApiResponse} containing a {@link AccountSectionResponse}
     *         with the requested accounts' response.
     */
    ApiResponse<AccountSectionResponse> fetchAccounts(Role role, Integer page, Integer size, String alphabet);

    /**
     * Fetches a paginated list of platform accounts based on the given user role and search query.
     *
     * @param role The role of the user requesting the accounts.
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @param query Query string to search for.
     * @return An {@link ApiResponse} containing a {@link AccountSectionResponse}
     *         with the requested accounts' response.
     */
    ApiResponse<AccountSectionResponse> search(Role role, Integer page, Integer size, String query);
}