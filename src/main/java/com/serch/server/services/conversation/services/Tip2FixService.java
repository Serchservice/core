package com.serch.server.services.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.conversation.requests.CheckTip2FixSessionRequest;

import java.util.UUID;

public interface Tip2FixService {
    ApiResponse<String> checkSession(CheckTip2FixSessionRequest request);
    void pay(UUID sender, UUID receiver);
}
