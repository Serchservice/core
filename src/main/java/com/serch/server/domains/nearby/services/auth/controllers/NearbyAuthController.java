package com.serch.server.domains.nearby.services.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.auth.requests.NearbyDeviceRequest;
import com.serch.server.domains.nearby.services.auth.services.NearbyAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/nearby")
public class NearbyAuthController {
    private final NearbyAuthService service;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UUID>> register(@RequestBody NearbyDeviceRequest request) {
        ApiResponse<UUID> response = service.register(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}