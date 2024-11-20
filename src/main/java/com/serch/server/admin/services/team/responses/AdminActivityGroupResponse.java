package com.serch.server.admin.services.team.responses;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminActivityGroupResponse {
    private String label;
    private LocalDateTime createdAt;
    private List<AdminActivityResponse> activities;
}