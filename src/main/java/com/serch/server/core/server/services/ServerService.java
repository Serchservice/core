package com.serch.server.core.server.services;

import com.serch.server.bases.ApiResponse;

import java.util.Map;

public interface ServerService {
    ApiResponse<Map<String, Object>> getMemoryStatistics();
    ApiResponse<String> generateOpenApiSpecification(Boolean isJson);
}
