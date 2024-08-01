package com.serch.server.admin.services.permission.requests;

import com.serch.server.admin.enums.Permission;
import com.serch.server.admin.enums.PermissionScope;
import lombok.Data;

import java.util.List;

@Data
public class PermissionScopeRequest {
    private PermissionScope scope;
    private String account;
    private List<Permission> permissions;
}