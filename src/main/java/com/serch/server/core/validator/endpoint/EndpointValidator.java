package com.serch.server.core.validator.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EndpointValidator implements EndpointValidatorService {
    @Override
    public boolean isDrivePermitted(String path) {
        List<String> endpoints = List.of("/location/search", "/shop/search", "/shop/drive/categories");

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
}