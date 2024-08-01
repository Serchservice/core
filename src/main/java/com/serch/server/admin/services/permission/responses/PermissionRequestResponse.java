package com.serch.server.admin.services.permission.responses;

import com.serch.server.admin.enums.Permission;
import com.serch.server.admin.enums.PermissionScope;
import com.serch.server.admin.enums.PermissionStatus;
import lombok.Data;

@Data
public class PermissionRequestResponse {
    private Long id;
    private String message;
    private String label;
    private String account;
    private PermissionStatus status;
    private PermissionScope scope;
    private Permission permission;
}