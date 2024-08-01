package com.serch.server.admin.services.scopes.admin;

import com.serch.server.admin.services.account.requests.AdminProfileUpdateRequest;
import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.admin.requests.ChangeRoleRequest;
import com.serch.server.admin.services.scopes.admin.requests.ChangeStatusRequest;
import com.serch.server.admin.services.scopes.admin.responses.AdminScopeResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.storage.requests.FileUploadRequest;

import java.util.List;
import java.util.UUID;

public interface AdminScopeService {
    /**
     * Fetch the admin response data of the given id
     *
     * @param id The admin id in {@link UUID}
     *
     * @return {@link ApiResponse} of {@link AdminScopeResponse}
     */
    ApiResponse<AdminScopeResponse> fetch(UUID id);

    /**
     * Fetch authentication chart data for a particular year in the admin record with admin id
     *
     * @param id The admin id in {@link UUID}
     * @param year The year data being requested
     *
     * @return List of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> fetchAuthChart(UUID id, Integer year);

    /**
     * Fetch account status chart data for a particular year in the admin record with admin id
     *
     * @param id The admin id in {@link UUID}
     * @param year The year data being requested
     *
     * @return List of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> fetchAccountStatusChart(UUID id, Integer year);

    /**
     * Change the avatar of an admin
     *
     * @param request The {@link FileUploadRequest} request data
     * @param id The admin id for the avatar upload
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> changeAvatar(FileUploadRequest request, UUID id);

    /**
     * Change the account status of an admin
     *
     * @param request The {@link ChangeStatusRequest} request data with the preferred status
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> changeStatus(ChangeStatusRequest request);

    /**
     * Change the role of an admin
     *
     * @param request The {@link ChangeRoleRequest} request data with the preferred role and other details
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> changeRole(ChangeRoleRequest request);

    /**
     * Toggle the constraint on admin MFA
     *
     * @param id The admin id
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<Boolean> toggle(UUID id);

    /**
     * Update the profile information of the admin by id
     *
     * @param request The {@link AdminProfileUpdateRequest} request data
     * @param id The admin id
     *
     * @return {@link ApiResponse} of failure or success
     */
    ApiResponse<String> update(AdminProfileUpdateRequest request, UUID id);

    /**
     * Delete an admin account
     *
     * @param id The admin id
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> delete(UUID id);
}
