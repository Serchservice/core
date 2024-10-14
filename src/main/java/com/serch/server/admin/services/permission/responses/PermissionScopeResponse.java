package com.serch.server.admin.services.permission.responses;

import com.serch.server.admin.enums.PermissionScope;
import lombok.Data;

@Data
public class PermissionScopeResponse {
    private String name;
    private PermissionScope scope;
}
