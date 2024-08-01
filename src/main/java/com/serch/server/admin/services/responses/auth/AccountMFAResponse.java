package com.serch.server.admin.services.responses.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountMFAResponse {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String id;
}
