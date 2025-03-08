package com.serch.server.core.validator.implementations;

import com.serch.server.core.validator.EndpointValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EndpointValidator implements EndpointValidatorService {
    @Override
    public boolean isDrivePermitted(String path) {
        List<String> endpoints = List.of(
                "/api/v1/location/search",
                "/api/v1/shop/search",
                "/api/v1/shop/drive/categories",
                "/api/v1/auth/nearby",
                "/api/v1/nearby"
        );

        return endpoints.stream().anyMatch(path::startsWith);
    }

    @Override
    public boolean isGuestPermitted(String path) {
        List<String> endpoints = List.of(
                "/api/v1/guest/",
                "/api/v1/switch/",
                "/api/v1/trip/shared/",
                "/api/v1/rating/rate/",
                "/api/v1/location/search/",
                "/api/v1/rating/app",
                "/api/v1/banking/",
                "/api/v1/trip/invite/",
                "/api/v1/trip/history",
                "/api/v1/trip/end",
                "/api/v1/trip/cancel",
                "/api/v1/trip/auth",
                "/api/v1/trip/shared/access",
                "/api/v1/trip/shared/auth",
                "/api/v1/trip/shared/cancel"
        );

        return endpoints.stream().anyMatch(path::startsWith);
    }

    @Override
    public boolean isSwaggerPermitted(String path) {
        String[] SWAGGER_ENDPOINTS = new String[]{
                "/swagger-ui",
                "/webjars",
                "/configuration",
                "/swagger-resources",
                "/v2/api-docs",
                "/v3/api-docs",
                "/api/v1/server"
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

    @Override
    public boolean isGoAuthenticatedOnly(String path) {
        String[] GO_ENDPOINTS = new String[]{
                "/api/v1/go/",
        };

        return Arrays.stream(GO_ENDPOINTS).anyMatch(path::startsWith);
    }

    @Override
    public boolean isGoAllowedOnly(String path) {
        String[] GO_ENDPOINTS = new String[]{
                "/api/v1/auth/go/",
                "/api/v1/go/activity/all",
                "/api/v1/go/bcap/all",
                "/api/v1/go/interest/all",
                "/api/v1/go/interest/category/all",
                "/api/v1/go/comment/",
                "/api/v1/go/rating/",
                "/api/v1/go/bcap/",
                "/api/v1/go/activity/"
        };

        return Arrays.stream(GO_ENDPOINTS).anyMatch(path::startsWith);
    }
}