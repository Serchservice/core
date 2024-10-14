package com.serch.server.admin.services.permission.requests;

import com.serch.server.admin.enums.Permission;
import lombok.Data;

@Data
public class PermissionRequestDto {
    private Permission permission;
    private Long id;
}
