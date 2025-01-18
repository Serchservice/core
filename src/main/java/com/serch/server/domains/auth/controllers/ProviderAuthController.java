package com.serch.server.domains.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.auth.requests.*;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.auth.responses.PendingRegistrationResponse;
import com.serch.server.domains.auth.services.AuthService;
import com.serch.server.domains.auth.services.ProviderAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/provider")
public class ProviderAuthController {
    private final ProviderAuthService providerAuthService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody RequestLogin request) {
        ApiResponse<AuthResponse> response = providerAuthService.login(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<PendingRegistrationResponse>> saveProfile(@RequestBody RequestProviderProfile request) {
        ApiResponse<PendingRegistrationResponse> response = authService.saveProfile(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/category")
    public ResponseEntity<ApiResponse<PendingRegistrationResponse>> saveCategory(@RequestBody RequestSerchCategory request) {
        ApiResponse<PendingRegistrationResponse> response = authService.saveCategory(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> savePlan(@RequestBody RequestAdditionalInformation auth) {
        ApiResponse<AuthResponse> response = providerAuthService.signup(auth);
        return new ResponseEntity<>(response, response.getStatus());
    }
}