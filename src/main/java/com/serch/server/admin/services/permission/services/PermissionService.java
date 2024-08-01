package com.serch.server.admin.services.permission.services;

import com.serch.server.admin.enums.Permission;
import com.serch.server.admin.enums.PermissionScope;
import com.serch.server.admin.enums.PermissionType;
import com.serch.server.admin.models.*;
import com.serch.server.admin.services.permission.requests.PermissionRequest;
import com.serch.server.admin.services.permission.requests.PermissionScopeRequest;
import com.serch.server.admin.services.permission.responses.PermissionRequestGroupResponse;
import com.serch.server.admin.services.permission.responses.PermissionScopeResponse;
import com.serch.server.admin.services.scopes.admin.requests.UpdatePermissionRequest;
import com.serch.server.bases.ApiResponse;

import java.util.List;

public interface PermissionService {
    /**
     * Creates a new permission scope or gets an existing one for the admin
     *
     * @param scope The {@link PermissionScope} to be granted
     * @param admin The {@link Admin} which the scope will be tied to
     * @param account The account for which the admin is requesting scope for (If it is an {@link PermissionScope#INDIVIDUAL})
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
     * Gets all the permissions granted to the logged in admin based
     * on {@link PermissionType#CLUSTER} or {@link PermissionType#SPECIFIC}
     *
     * @param admin The {@link Admin} whose permission scopes are being requested for
     * @param type The {@link PermissionType} being requested for
     *
     * @return List of {@link PermissionScopeResponse}
     */
    List<PermissionScopeResponse> getScopes(Admin admin, PermissionType type);

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
     * @param id The permission request id
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> grant(Long id);

    /**
     * Decline a pending permission request
     *
     * @param id The permission request id
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> decline(Long id);

    /**
     * Fetch all permission requests for the logged-in admin to
     * either {@link PermissionService#decline(Long)} or {@link PermissionService#grant(Long)}
     *
     * @return {@link ApiResponse} list of {@link PermissionRequestGroupResponse}
     */
    ApiResponse<List<PermissionRequestGroupResponse>> requests();

    /**
     * Update, add and remove the permissions assigned to an admin
     *
     * @param request The {@link UpdatePermissionRequest} request data with the needed permissions
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> updatePermissions(UpdatePermissionRequest request);
}