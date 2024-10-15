package com.serch.server.admin.services.permission.responses;

import lombok.Data;

@Data
public class PermissionRequestAccountDetails {
    private String name;
    private String role;
    private String account;
}
