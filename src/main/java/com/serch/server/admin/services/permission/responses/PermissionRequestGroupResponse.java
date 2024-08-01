package com.serch.server.admin.services.permission.responses;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PermissionRequestGroupResponse {
    private String label;
    private LocalDateTime createdAt;
    private List<PermissionRequestResponse> requests;
}
