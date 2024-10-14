package com.serch.server.admin.services.permission.services;

import com.serch.server.admin.enums.Permission;
import com.serch.server.admin.enums.PermissionScope;
import com.serch.server.admin.models.*;
import com.serch.server.admin.services.permission.requests.PermissionRequest;
import com.serch.server.admin.services.permission.requests.PermissionScopeRequest;
import com.serch.server.admin.services.permission.responses.*;
import com.serch.server.admin.services.permission.requests.UpdatePermissionRequest;
import com.serch.server.bases.ApiResponse;

import java.util.List;

public interface PermissionService {
    /**
     * Creates a new permission scope or gets an existing one for the admin
     *
     * @param scope The {@link PermissionScope} to be granted
     * @param admin The {@link Admin} which the scope will be tied to
     * @param account The account for which the admin is requesting scope for
     *
     * @return {@link GrantedPermissionScope}
     */
    GrantedPermissionScope create(PermissionScope scope, Admin admin, String account);

    /**
     * Attach a new permission or get an existing one for the permission scope
     *
     * @param scope The {@link GrantedPermissionScope} which the permission will be tied to
     * @param permission The {@link Permission} to be granted
     *
     * @return {@link GrantedPermission}
     */
    GrantedPermission create(GrantedPermissionScope scope, Permission permission);

    /**
     * Attach parent admin existing scope permissions to the new child admin
     *
     * @param parent The existing {@link Admin} who is adding the {@param child}
     * @param child The new {@link Admin} being added by the {@param parent}
     */
    void attach(Admin parent, Admin child);

    /**
     * Creates a new permission scope for the new admin added by an existing admin.
     * The scope comes from the request made from the admin add api.
     *
     * @param scopes The list of {@link PermissionScopeRequest}
     *               to be added to the {@link Admin} being created
     * @param admin The {@link Admin} to be attached to the granted permission
     */
    void create(List<PermissionScopeRequest> scopes, Admin admin);

    /**
     * Gets all the cluster permissions granted to the logged in admin
     *
     * @param admin The {@link Admin} whose permission scopes are being requested for
     *
     * @return List of {@link GrantedPermissionScopeResponse}
     */
    List<GrantedPermissionScopeResponse> getGrantedClusterPermissions(Admin admin);

    /**
     * Gets all the specific permissions granted to the logged in admin
     *
     * @param admin The {@link Admin} whose permission scopes are being requested for
     *
     * @return List of {@link GrantedPermissionScopeResponse}
     */
    List<SpecificPermissionResponse> getGrantedSpecificPermissions(Admin admin);

    /**
     * Make a permission request
     *
     * @param request The {@link PermissionRequest} data
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> request(PermissionRequest request);

    /**
     * Grants a pending permission request
     *
     * @param expiration The period for permission expiration
     * @param id The permission request id
     *
     * @return {@link ApiResponse} list of {@link PermissionRequestGroupResponse}
     */
    ApiResponse<List<PermissionRequestGroupResponse>> grant(Long id, Long expiration);

    /**
     * Decline a pending permission request
     *
     * @param id The permission request id
     *
     * @return {@link ApiResponse} list of {@link PermissionRequestGroupResponse}
     */
    ApiResponse<List<PermissionRequestGroupResponse>> decline(Long id);

    /**
     * Fetch all permission requests for the logged-in admin to
     * either {@link PermissionService#decline(Long)} or {@link PermissionService#grant(Long, Long)}
     *
     * @return {@link ApiResponse} list of {@link PermissionRequestGroupResponse}
     */
    ApiResponse<List<PermissionRequestGroupResponse>> requests();

    /**
     * Revoke a granted permission
     *
     * @return {@link ApiResponse} list of {@link PermissionRequestGroupResponse}
     */
    ApiResponse<List<PermissionRequestGroupResponse>> revoke(Long id);

    /**
     * Cancel a requested permission
     *
     * @return {@link ApiResponse} list of {@link PermissionRequestGroupResponse}
     */
    ApiResponse<List<PermissionRequestGroupResponse>> cancel(Long id);

    /**
     * Update, add and remove the permissions assigned to an admin
     *
     * @param request The {@link UpdatePermissionRequest} request data with the needed permissions
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> updatePermissions(UpdatePermissionRequest request);

    /**
     * Get all the permission scopes in the Serchservice platform
     *
     * @return {@link ApiResponse} list of {@link PermissionScopeResponse}
     */
    ApiResponse<List<PermissionScopeResponse>> getAllScopes();

    /**
     * Search for a user or admin using the provided id to fetch the user details
     *
     * @param id The id of the user to search for.
     *
     * @return {@link ApiResponse} of {@link PermissionAccountSearchResponse}
     */
    ApiResponse<PermissionAccountSearchResponse> search(String id);

    /**
     * This will revoke any permission with an expiry date or period
     */
    void revokeExpiredPermissions();
}