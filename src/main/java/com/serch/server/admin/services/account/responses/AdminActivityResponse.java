package com.serch.server.admin.services.account.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminActivityResponse {
    private Long id;
    private String activity;
    private String associated;
    private String label;
    private String header;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}