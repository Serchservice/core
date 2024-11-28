package com.serch.server.core.validator.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EndpointValidator implements EndpointValidatorService {
    @Override
    public boolean isDrivePermitted(String path) {
        List<String> endpoints = List.of("/location/search", "/shop/search", "/shop/drive/categories", "/auth/nearby", "/nearby");

        return endpoints.stream().anyMatch(path::startsWith);
    }

    @Override
    public boolean isGuestPermitted(String path) {
        List<String> endpoints = List.of(
                "/guest/", "/switch/", "/trip/shared/", "/rating/rate/", "/location/search/",
                "/rating/app", "/banking/", "/trip/invite/", "/trip/history", "/trip/end", "/trip/cancel",
                "/trip/auth", "/trip/shared/access", "/trip/shared/auth", "/trip/shared/cancel"
        );

        return endpoints.stream().anyMatch(path::startsWith);
    }

    @Override
    public boolean isSwaggerPermitted(String path) {
        String[] SWAGGER_ENDPOINTS = new String[]{
                "/swagger-ui",
                "/webjars",
                "configuration",
                "/swagger-resources",
                "/v2/api-docs",
                "/v3/api-docs",
        };

        return Arrays.stream(SWAGGER_ENDPOINTS).anyMatch(path::startsWith);
    }

    @Override
    public boolean isSocketPermitted(String path) {
        String[] WEB_SOCKET_ENDPOINTS = new String[]{
                "/ws:serch",
                "/ws:trip",
        };

        return Arrays.stream(WEB_SOCKET_ENDPOINTS).anyMatch(path::startsWith);
    }
}