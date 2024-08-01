package com.serch.server.admin.services.scopes.support.services;

import com.serch.server.admin.services.scopes.support.responses.ComplaintScopeResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

public interface ComplaintScopeService {
    /**
     * Get the list of complaints in the Serch platform
     *
     * @return {@link ApiResponse} list of {@link ComplaintScopeResponse}
     */
    ApiResponse<List<ComplaintScopeResponse>> complaints();

    /**
     * Update the status of a complaint in Serch
     *
     * @param id The {@link com.serch.server.models.company.Complaint} id to update
     *
     * @return {@link ApiResponse} list of {@link ComplaintScopeResponse}
     */
    ApiResponse<List<ComplaintScopeResponse>> resolve(String id);

    /**
     * Get a complaint in Serch
     *
     * @param emailAddress The emailAddress to fetch its complaints
     *
     * @return {@link ApiResponse} of {@link ComplaintScopeResponse}
     */
    ApiResponse<ComplaintScopeResponse> complaint(String emailAddress);
}
