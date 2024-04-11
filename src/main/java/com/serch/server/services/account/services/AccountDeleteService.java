package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;

import java.util.UUID;

public interface AccountDeleteService {
    ApiResponse<String> delete(UUID id);
    ApiResponse<String> delete();
}
