package com.serch.server.admin.services.account.responses;

import com.serch.server.admin.services.permission.responses.GrantedPermissionScopeResponse;
import com.serch.server.admin.services.permission.responses.SpecificPermissionResponse;
import com.serch.server.enums.auth.Role;
import lombok.Data;

import java.util.List;

@Data
public class AdminTeamResponse {
    private Role role;
    private String position;
    private String department;
    private List<GrantedPermissionScopeResponse> cluster;
    private List<SpecificPermissionResponse> specific;
}
