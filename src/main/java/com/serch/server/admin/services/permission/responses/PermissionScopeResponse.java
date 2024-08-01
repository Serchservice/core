package com.serch.server.admin.services.permission.responses;

import com.serch.server.admin.enums.PermissionScope;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PermissionScopeResponse {
    private Long id;
    private PermissionScope scope;
    private String name;
    private String account;
    private String createdAt;
    private String updatedAt;
    private List<PermissionResponse> permissions = new ArrayList<>();
}