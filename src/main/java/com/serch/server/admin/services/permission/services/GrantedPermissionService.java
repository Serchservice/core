package com.serch.server.admin.services.permission.services;

import com.serch.server.admin.models.Admin;
import com.serch.server.admin.services.permission.responses.GrantedPermissionScopeResponse;
import com.serch.server.admin.services.permission.responses.SpecificPermissionResponse;

import java.util.List;

public interface GrantedPermissionService {
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
}
