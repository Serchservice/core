package com.serch.server.admin.services.permission.requests;

import lombok.Data;

import java.util.List;

@Data
public class SpecificPermissionRequest {
    private String account;
    private List<PermissionScopeRequest> scopes;
}
