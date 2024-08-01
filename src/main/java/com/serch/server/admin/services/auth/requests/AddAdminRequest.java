package com.serch.server.admin.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.permission.requests.PermissionScopeRequest;
import com.serch.server.enums.auth.Role;
import lombok.Data;

import java.util.List;

@Data
public class AddAdminRequest {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email_address")
    private String emailAddress;
    private Role role;
    private String position;
    private String department;

    private List<PermissionScopeRequest> scopes;
}