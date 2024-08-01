package com.serch.server.admin.services.responses.auth;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AccountRefreshTokenResponse {
    private String id;
    private String token;
    private Date updatedAt;
    private Date createdAt;
    private Boolean revoked;
    private List<AccountRefreshTokenResponse> refreshTokens;
}