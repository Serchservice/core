package com.serch.server.admin.services.permission.responses;

import com.serch.server.admin.enums.Permission;
import lombok.Data;

@Data
public class PermissionResponse {
    private Long id;
    private String createdAt;
    private String updatedAt;
    private String expiration;
    private Permission permission;
}