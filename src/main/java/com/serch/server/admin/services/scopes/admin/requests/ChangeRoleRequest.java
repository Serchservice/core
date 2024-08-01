package com.serch.server.admin.services.scopes.admin.requests;

import com.serch.server.enums.auth.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class ChangeRoleRequest {
    private UUID id;
    private Role role;
    private String position;
    private String department;
}
