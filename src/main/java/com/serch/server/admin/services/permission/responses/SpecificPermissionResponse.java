package com.serch.server.admin.services.permission.responses;

import lombok.Data;

import java.util.List;

@Data
public class SpecificPermissionResponse {
    private String account;
    private String name;
    private List<GrantedPermissionScopeResponse> scopes;
}