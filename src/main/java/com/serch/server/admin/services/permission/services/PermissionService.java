package com.serch.server.admin.services.permission.services;

import com.serch.server.admin.enums.Permission;
import com.serch.server.admin.enums.PermissionScope;
import com.serch.server.admin.models.*;
import com.serch.server.admin.services.account.responses.AdminTeamResponse;
import com.serch.server.admin.services.permission.requests.PermissionRequest;
import com.serch.server.admin.services.permission.requests.PermissionScopeRequest;
import com.serch.server.admin.services.permission.responses.*;
import com.serch.server.admin.services.permission.requests.UpdatePermissionRequest;
import com.serch.server.bases.ApiResponse;

import java.util.List;

/**
 * Service interface for managing permissions within the Serch organization.
 * This interface provides methods to create, grant, decline, and manage permissions
 * and permission scopes for admins, allowing for detailed control over user access
 * and capabilities within the system.
 * Implementations of this interface encapsulate the business logic related to
 * permissions, ensuring efficient handling of permission-related operations.
 */
public interface PermissionService {

    /**
     * Creates a new permission scope or retrieves an existing one for the specified admin.
     * This method establishes the scope of permissions that the admin will have
     * access to, which can include specific actions or resources within the system.
     *
     * @param scope The {@link PermissionScope} to be granted to the admin.
     * @param admin The {@link Admin} to whom the scope will be tied.
     * @param account The account for which the admin is requesting the scope.
     * @return {@link GrantedPermissionScope} representing the granted permission scope.
     */
    GrantedPermissionScope create(PermissionScope scope, Admin admin, String account);

    /**
     * Attaches a new permission or retrieves an existing one for the specified permission scope.
     * This method allows for the assignment of specific permissions within the established scope,
     * ensuring that the admin can perform the necessary actions.
     *
     * @param scope The {@link GrantedPermissionScope} which the permission will be tied to.
     * @param permission The {@link Permission} to be granted.
     * @return {@link GrantedPermission} representing the granted permission.
     */
    GrantedPermission create(GrantedPermissionScope scope, Permission permission);

    /**
     * Attaches the existing permissions of a parent admin to a new child admin.
     * This method facilitates the inheritance of permissions, ensuring that new admins
     * can benefit from the permissions already established by their parent admins.
     *
     * @param parent The existing {@link Admin} who is adding the child admin.
     * @param child The new {@link Admin} being added by the parent.
     */
    void attach(Admin parent, Admin child);

    /**
     * Creates a new permission scope for the new admin being added by an existing admin.
     * The scopes are derived from the request made to the admin add API.
     *
     * @param scopes The list of {@link PermissionScopeRequest} to be added to the new admin.
     * @param admin The {@link Admin} to be attached to the granted permissions.
     */
    void create(List<PermissionScopeRequest> scopes, Admin admin);

    /**
     * Submits a permission request based on the specified data.
     * This method allows admins to request specific permissions, which can be granted
     * or denied based on the organization's policies.
     *
     * @param request The {@link PermissionRequest} containing the necessary data for the request.
     * @return {@link ApiResponse} indicating the success or failure of the permission request.
     */
    ApiResponse<String> request(PermissionRequest request);

    /**
     * Grants a pending permission request based on its ID and specifies the expiration period.
     * This method allows for the approval of permission requests, enabling admins to gain
     * the requested access for a defined period.
     *
     * @param expiration The expiration period for the granted permission in ISO format (e.g., "2024-10-15T08:55:00.000Z").
     * @param id The ID of the permission request to be granted.
     * @return {@link ApiResponse} containing a list of {@link PermissionRequestGroupResponse} representing the granted permissions.
     */
    ApiResponse<List<PermissionRequestGroupResponse>> grant(Long id, String expiration);

    /**
     * Declines a pending permission request based on its ID.
     * This method allows admins to deny permission requests, ensuring that access
     * is granted only to those who meet the necessary criteria.
     *
     * @param id The ID of the permission request to be declined.
     * @return {@link ApiResponse} containing a list of {@link PermissionRequestGroupResponse} representing the declined permissions.
     */
    ApiResponse<List<PermissionRequestGroupResponse>> decline(Long id);

    /**
     * Fetches all permission requests for the logged-in admin.
     * This method allows admins to view pending permission requests that they can
     * either approve or decline, facilitating efficient permission management.
     *
     * @return {@link ApiResponse} containing a list of {@link PermissionRequestGroupResponse} representing all pending requests.
     */
    ApiResponse<List<PermissionRequestGroupResponse>> requests();

    /**
     * Revokes a granted permission based on its ID.
     * This method allows for the removal of permissions that are no longer necessary
     * or that have been misused, ensuring tight control over admin access.
     *
     * @param id The ID of the permission to be revoked.
     * @return {@link ApiResponse} containing a list of {@link PermissionRequestGroupResponse} representing the revoked permissions.
     */
    ApiResponse<List<PermissionRequestGroupResponse>> revoke(Long id);

    /**
     * Cancels a requested permission based on its ID.
     * This method allows admins to withdraw permission requests that are no longer needed,
     * helping to streamline permission management.
     *
     * @param id The ID of the permission request to be canceled.
     * @return {@link ApiResponse} containing a list of {@link PermissionRequestGroupResponse} representing the canceled permissions.
     */
    ApiResponse<List<PermissionRequestGroupResponse>> cancel(Long id);

    /**
     * Updates the permissions assigned to an admin by adding, removing, or modifying
     * the permissions based on the provided request data.
     *
     * @param request The {@link UpdatePermissionRequest} containing the necessary data for the update.
     * @return {@link ApiResponse} containing the updated {@link AdminTeamResponse} with the current permissions.
     */
    ApiResponse<AdminTeamResponse> updatePermissions(UpdatePermissionRequest request);

    /**
     * Retrieves all permission scopes available in the Serch service platform.
     * This method provides admins with a comprehensive view of the permission scopes,
     * enabling effective management of permissions across the organization.
     *
     * @return {@link ApiResponse} containing a list of {@link PermissionScopeResponse} representing all available scopes.
     */
    ApiResponse<List<PermissionScopeResponse>> getAllScopes();

    /**
     * Searches for a user or admin using the provided ID to fetch their details.
     * This method enables admins to find specific users or admins within the system
     * based on their unique identifiers.
     *
     * @param id The ID of the user to search for.
     * @return {@link ApiResponse} containing the {@link PermissionAccountSearchResponse} with the user's details.
     */
    ApiResponse<PermissionAccountSearchResponse> search(String id);

    /**
     * Revokes any permissions that have expired based on their expiration date or period.
     * This method helps to ensure that only current permissions are active,
     * maintaining security and control over admin access within the system.
     */
    void revokeExpiredPermissions();
}