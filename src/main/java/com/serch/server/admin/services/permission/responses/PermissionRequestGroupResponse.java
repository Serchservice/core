package com.serch.server.admin.services.permission.responses;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class PermissionRequestGroupResponse {
    private String label;
    private ZonedDateTime createdAt;
    private List<PermissionRequestResponse> requests;
}
