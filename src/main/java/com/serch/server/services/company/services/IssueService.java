package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.IssueRequest;

/**
 * Service interface for handling issue-related operations.
 *
 * @see com.serch.server.services.company.services.implementations.IssueImplementation
 */
public interface IssueService {

    /**
     * Lodges an issue for a specific product.
     *
     * @param request The IssueRequest object containing issue details.
     * @return ApiResponse containing a message with the issue submission status.
     *
     * @see ApiResponse
     * @see IssueRequest
     */
    ApiResponse<String> lodgeIssue(IssueRequest request);
}