package com.serch.server.admin.services.permission.responses;

import com.serch.server.admin.enums.PermissionScope;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GrantedPermissionScopeResponse {
    private Long id;
    private PermissionScope scope;
    private String createdAt;
    private String updatedAt;
    private List<PermissionResponse> permissions = new ArrayList<>();
}