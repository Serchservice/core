package com.serch.server.admin.services.permission.requests;

import com.serch.server.admin.enums.PermissionScope;
import lombok.Data;

import java.util.List;

@Data
public class PermissionScopeRequest {
    private Long id;
    private PermissionScope scope;
    private List<PermissionRequestDto> permissions;
}