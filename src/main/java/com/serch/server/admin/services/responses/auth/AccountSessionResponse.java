package com.serch.server.admin.services.responses.auth;

import com.serch.server.enums.auth.AuthLevel;
import com.serch.server.enums.auth.AuthMethod;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AccountSessionResponse {
    private String name;
    private String platform;
    private String osv;
    private String os;
    private AuthMethod method;
    private AuthLevel level;
    private String ipAddress;
    private String host;
    private String device;
    private String id;
    private Boolean revoked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AccountRefreshTokenResponse> refreshTokens;
}
