package com.serch.server.admin.services.auth.controller;

import com.serch.server.admin.services.auth.requests.*;
import com.serch.server.admin.services.auth.services.AdminAuthService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.auth.responses.MFADataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/admin")
public class AdminAuthController {
    private final AdminAuthService service;

    @GetMapping("/invite/verify")
    public ResponseEntity<ApiResponse<MFADataResponse>> verifyLink(@RequestParam String secret) {
        ApiResponse<MFADataResponse> response = service.verifyLink(secret);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/invite/resend")
    public ResponseEntity<ApiResponse<String>> resendInvite(@RequestParam UUID id) {
        ApiResponse<String> response = service.resendInvite(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/resend")
    public ResponseEntity<ApiResponse<String>> resend(@RequestParam String emailAddress) {
        ApiResponse<String> response = service.resend(emailAddress);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/password/reset/verify")
    public ResponseEntity<ApiResponse<String>> verifyResetLink(@RequestParam String secret) {
        ApiResponse<String> response = service.verifyResetLink(secret);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/password/reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam UUID id) {
        ApiResponse<String> response = service.resetPassword(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<AuthResponse>> resetPassword(@RequestBody AdminResetPasswordRequest reset) {
        ApiResponse<AuthResponse> response = service.resetPassword(reset);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody AdminLoginRequest request) {
        ApiResponse<String> response = service.login(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/super/signup")
    public ResponseEntity<ApiResponse<MFADataResponse>> signup(@RequestBody AdminSignupRequest request) {
        ApiResponse<MFADataResponse> response = service.signup(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/invite")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<String>> add(@RequestBody AddAdminRequest request) {
        ApiResponse<String> response = service.add(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<AuthResponse>> confirm(@RequestBody AdminAuthTokenRequest request) {
        ApiResponse<AuthResponse> response = service.confirm(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/invite/setup")
    public ResponseEntity<ApiResponse<AuthResponse>> finishSignup(@RequestBody FinishAdminSetupRequest request) {
        ApiResponse<AuthResponse> response = service.finishSignup(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}