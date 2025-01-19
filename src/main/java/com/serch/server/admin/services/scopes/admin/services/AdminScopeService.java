package com.serch.server.admin.services.scopes.admin.services;

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
     * Fetches the admin response response for the specified admin ID.
     * This method retrieves all relevant information related to the admin,
     * including their profile details, permissions, and roles within the system.
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @param id The unique identifier of the admin in {@link UUID} format.
     *           This ID must correspond to an existing admin in the system.
     * @return {@link ApiResponse} containing an {@link AdminScopeResponse}
     *         with the admin's details. The response may include error messages
     *         if the admin ID does not exist.
     */
    ApiResponse<AdminScopeResponse> fetch(UUID id, Integer page, Integer size);

    /**
     * Fetches authentication chart response for a particular year
     * for the specified admin ID. This method provides insights
     * into the authentication patterns of the admin over the
     * given year, such as successful logins, failed attempts, etc.
     *
     * @param id The unique identifier of the admin in {@link UUID} format.
     *           This ID must correspond to an existing admin in the system.
     * @param year The year for which the authentication response is requested.
     *             This should be a valid year (e.g., 2023).
     * @return {@link ApiResponse} containing a list of {@link ChartMetric}
     *         that represents the authentication response for the specified year.
     *         If the year is invalid or the admin ID does not exist, an error message is returned.
     */
    ApiResponse<List<ChartMetric>> fetchAuthChart(UUID id, Integer year);

    /**
     * Fetches account status chart response for a particular year
     * related to the specified admin ID. This method offers insights
     * into the status changes of the admin account over the given year,
     * such as account activation, deactivation, or any other relevant
     * status changes.
     *
     * @param id The unique identifier of the admin in {@link UUID} format.
     *           This ID must correspond to an existing admin in the system.
     * @param year The year for which the account status response is requested.
     *             This should be a valid year (e.g., 2023).
     * @return {@link ApiResponse} containing a list of {@link ChartMetric}
     *         that represents the account status response for the specified year.
     *         If the year is invalid or the admin ID does not exist, an error message is returned.
     */
    ApiResponse<List<ChartMetric>> fetchAccountStatusChart(UUID id, Integer year);

    /**
     * Changes the avatar of the specified admin. This method allows the admin
     * to upload a new avatar image, which will be associated with their profile.
     *
     * @param request The {@link FileUploadRequest} containing the image file
     *                and any additional response required for the upload.
     * @param id The unique identifier of the admin in {@link UUID} format.
     *           This ID must correspond to an existing admin in the system.
     * @return {@link ApiResponse} indicating the success or failure of the avatar change operation.
     *         If successful, the response may contain a message indicating success,
     *         otherwise, it may include an error message.
     */
    ApiResponse<String> changeAvatar(FileUploadRequest request, UUID id);

    /**
     * Changes the account status of the specified admin. This method can be used
     * to activate, deactivate, or otherwise modify the admin's account status.
     *
     * @param request The {@link ChangeStatusRequest} containing the new preferred
     *                status and any related information for the admin's account.
     * @return {@link ApiResponse} indicating the success or failure of the status change operation.
     *         If successful, the response may contain a message indicating success,
     *         otherwise, it may include an error message.
     */
    ApiResponse<String> changeStatus(ChangeStatusRequest request);

    /**
     * Changes the role of the specified admin. This method allows for modification
     * of the admin's permissions and responsibilities by assigning a new role.
     *
     * @param request The {@link ChangeRoleRequest} containing the new preferred role
     *                and any other details required for the role change.
     * @return {@link ApiResponse} indicating the success or failure of the role change operation.
     *         If successful, the response may contain a message indicating success,
     *         otherwise, it may include an error message.
     */
    ApiResponse<String> changeRole(ChangeRoleRequest request);

    /**
     * Toggles the Multi-Factor Authentication (MFA) constraint on the specified admin.
     * This method can enable or disable MFA for the admin's account, enhancing or
     * reducing security measures as necessary.
     *
     * @param id The unique identifier of the admin in {@link UUID} format.
     *           This ID must correspond to an existing admin in the system.
     * @return {@link ApiResponse} indicating the success or failure of the toggle operation.
     *         If successful, the response may include a boolean indicating the new state of MFA,
     *         otherwise, it may include an error message.
     */
    ApiResponse<Boolean> toggle(UUID id);

    /**
     * Updates the profile information of the specified admin. This method allows
     * the admin to modify their personal details such as name, email, and other
     * profile attributes.
     *
     * @param request The {@link AdminProfileUpdateRequest} containing the updated
     *                profile information and any other necessary response.
     * @param id The unique identifier of the admin in {@link UUID} format.
     *           This ID must correspond to an existing admin in the system.
     * @return {@link ApiResponse} indicating the success or failure of the profile update operation.
     *         If successful, the response may contain a message indicating success,
     *         otherwise, it may include an error message.
     */
    ApiResponse<String> update(AdminProfileUpdateRequest request, UUID id);

    /**
     * Deletes the admin account identified by the specified admin ID.
     * This action will remove all associated response and permissions for the admin.
     *
     * @param id The unique identifier of the admin in {@link UUID} format.
     *           This ID must correspond to an existing admin in the system.
     * @return {@link ApiResponse} indicating the success or failure of the delete operation.
     *         If successful, the response may contain a message indicating success,
     *         otherwise, it may include an error message.
     */
    ApiResponse<String> delete(UUID id);
}