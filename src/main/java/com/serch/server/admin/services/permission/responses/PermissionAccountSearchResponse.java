package com.serch.server.admin.services.permission.responses;

import com.serch.server.admin.enums.PermissionScope;
import lombok.Data;

import java.util.List;

@Data
public class PermissionAccountSearchResponse {
    private String id;
    private String name;
    private String avatar;
    private String role;
    private List<PermissionScope> scopes;
}