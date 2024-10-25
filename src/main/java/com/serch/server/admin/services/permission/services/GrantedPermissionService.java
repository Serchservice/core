package com.serch.server.admin.services.permission.services;

import com.serch.server.admin.models.Admin;
import com.serch.server.admin.services.permission.responses.GrantedPermissionScopeResponse;
import com.serch.server.admin.services.permission.responses.SpecificPermissionResponse;

import java.util.List;

public interface GrantedPermissionService {
    /**
     * Retrieves all the cluster permissions granted to the currently logged-in admin.
     * This method queries the permission scopes associated with the provided admin,
     * returning a list of permissions that grant access to specific cluster resources
     * and functionalities.
     *
     * @param admin The {@link Admin} whose permission scopes are being requested for.
     *              This parameter must not be null and must represent a valid admin
     *              instance that is currently authenticated in the system.
     *
     * @return A list of {@link GrantedPermissionScopeResponse} instances representing
     *         the cluster permissions granted to the specified admin. The list may be empty
     *         if no permissions have been granted.
     *         The returned objects contain detailed information about each permission
     *         scope, including the permissions granted and their specific attributes.
     */
    List<GrantedPermissionScopeResponse> getGrantedClusterPermissions(Admin admin);

    /**
     * Retrieves all the specific permissions granted to the currently logged-in admin.
     * This method fetches the specific permissions that allow access to defined actions
     * and resources beyond the broader cluster permissions, giving a detailed view of
     * the admin's capabilities within the system.
     *
     * @param admin The {@link Admin} whose specific permissions are being requested for.
     *              This parameter must not be null and should represent a valid admin
     *              instance that is currently authenticated in the system.
     *
     * @return A list of {@link SpecificPermissionResponse} instances representing
     *         the specific permissions granted to the specified admin. The list may be empty
     *         if no specific permissions have been granted.
     *         Each returned object provides details about a specific permission, including
     *         its action type and associated resources.
     */
    List<SpecificPermissionResponse> getGrantedSpecificPermissions(Admin admin);
}