package com.serch.server.admin.services.permission.requests;

import lombok.Data;

import java.util.List;

@Data
public class PermissionRequest {
    private List<PermissionScopeRequest> cluster;
    private List<PermissionScopeRequest> specific;
}