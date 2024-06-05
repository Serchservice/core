package com.serch.server.services.verified;

import com.serch.server.bases.ApiResponse;

import java.util.Map;

public interface VerifiedService {
    ApiResponse<String> smileStatus(Map<Object, Object> data);
}
