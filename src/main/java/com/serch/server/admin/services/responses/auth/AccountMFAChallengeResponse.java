package com.serch.server.admin.services.responses.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountMFAChallengeResponse {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime verifiedAt;
    private String name;
    private String platform;
    private String osv;
    private String os;
    private String ipAddress;
    private String host;
    private String localHost;
    private String device;
    private String id;
}