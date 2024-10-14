package com.serch.server.admin.services.permission.requests;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdatePermissionRequest {
    private UUID id;
    private List<PermissionScopeRequest> cluster;
    private List<SpecificPermissionRequest> specific;
}
