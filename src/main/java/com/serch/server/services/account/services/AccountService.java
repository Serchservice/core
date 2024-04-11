package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;

import java.util.UUID;

public interface AccountService {
    ApiResponse<String> switchAccount(UUID id, boolean toUser);
}
