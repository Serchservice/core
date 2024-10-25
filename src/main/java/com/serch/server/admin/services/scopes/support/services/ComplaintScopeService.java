package com.serch.server.admin.services.scopes.support.services;

import com.serch.server.admin.services.scopes.support.responses.ComplaintScopeResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

/**
 * Service interface for managing complaint-related operations within the support scope.
 * Provides methods for retrieving complaints, updating their statuses, and fetching specific complaints.
 */
public interface ComplaintScopeService {

    /**
     * Retrieves the list of all complaints submitted on the Serch platform.
     * This method returns the details of each complaint, including information about the complaint's status,
     * the user who submitted it, and any relevant descriptions or metadata.
     *
     * @return an {@link ApiResponse} containing a list of {@link ComplaintScopeResponse},
     *         where each response provides the details of a specific complaint.
     */
    ApiResponse<List<ComplaintScopeResponse>> complaints();

    /**
     * Updates the status of a specified complaint.
     * This method allows resolving or updating the state of a complaint based on its ID.
     * It may include setting the complaint's status to "resolved," "pending," or other status types
     * defined by the system's business rules.
     *
     * @param id the unique identifier of the {@link com.serch.server.models.company.Complaint} to update.
     * @return an {@link ApiResponse} containing the updated list of {@link ComplaintScopeResponse},
     *         where each response represents the current state of the complaints after the update.
     */
    ApiResponse<List<ComplaintScopeResponse>> resolve(String id);

    /**
     * Retrieves a specific complaint based on the email address associated with the complaint submission.
     * This can be used to find the complaints submitted by a particular user.
     *
     * @param emailAddress the email address used to filter and fetch the complaint(s) related to the user.
     * @return an {@link ApiResponse} containing a {@link ComplaintScopeResponse} that provides details
     *         about the matching complaint. If multiple complaints exist for the same email, it returns the latest one.
     */
    ApiResponse<ComplaintScopeResponse> complaint(String emailAddress);
}