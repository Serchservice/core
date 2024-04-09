package com.serch.server.services.conversation.services;

import com.serch.backend.bases.ApiResponse;

import java.util.UUID;

public interface Tip2FixService {
    ApiResponse<String> checkSession(Integer duration, String channel);
    void pay(UUID provider, UUID user);
}
