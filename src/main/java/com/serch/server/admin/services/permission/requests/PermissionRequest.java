package com.serch.server.admin.services.permission.requests;

import lombok.Data;

@Data
public class PermissionRequest {
    private PermissionScopeRequest cluster;
    private SpecificPermissionRequest specific;
}