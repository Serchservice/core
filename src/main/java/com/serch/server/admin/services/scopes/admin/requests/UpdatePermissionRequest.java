package com.serch.server.admin.services.scopes.admin.requests;

import com.serch.server.admin.services.permission.responses.PermissionScopeResponse;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdatePermissionRequest {
    private UUID id;
    private List<PermissionScopeResponse> cluster;
    private List<PermissionScopeResponse> specific;
}
