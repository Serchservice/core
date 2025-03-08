package com.serch.server.domains.nearby.services.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.auth.requests.GoAuthTokenRequest;
import com.serch.server.domains.nearby.services.auth.requests.GoAuthRequest;
import com.serch.server.domains.nearby.services.auth.requests.GoPasswordRequest;
import com.serch.server.domains.nearby.services.auth.responses.GoAuthResponse;
import com.serch.server.domains.nearby.services.auth.services.GoUserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/go")
public class GoUserAuthController {
    private final GoUserAuthService service;

    @GetMapping("/resend")
    public ResponseEntity<ApiResponse<String>> resend(
            @RequestParam(name = "email_address") String emailAddress,
            @RequestParam(name = "is_signup") Boolean isSignup
    ) {
        ApiResponse<String> response = service.resend(emailAddress, isSignup);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/forgot_password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestParam(name = "email_address") String emailAddress) {
        ApiResponse<String> response = service.forgotPassword(emailAddress);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<GoAuthResponse>> refreshToken(@RequestBody GoAuthTokenRequest request) {
        ApiResponse<GoAuthResponse> response = service.refreshToken(request.token());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<GoAuthResponse>> login(@RequestBody GoAuthRequest request) {
        ApiResponse<GoAuthResponse> response = service.login(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody GoAuthRequest request) {
        ApiResponse<String> response = service.signup(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/signup/verify")
    public ResponseEntity<ApiResponse<GoAuthResponse>> verifySignupToken(@RequestBody GoAuthTokenRequest request) {
        ApiResponse<GoAuthResponse> response = service.verifySignupToken(request.emailAddress(), request.token());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/forgot_password/verify")
    public ResponseEntity<ApiResponse<String>> verifyToken(@RequestBody GoAuthTokenRequest request) {
        ApiResponse<String> response = service.verifyToken(request.emailAddress(), request.token());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/reset_password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody GoAuthRequest request) {
        ApiResponse<String> response = service.resetPassword(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/change_password")
    public ResponseEntity<ApiResponse<GoAuthResponse>> changePassword(@RequestBody GoPasswordRequest request) {
        ApiResponse<GoAuthResponse> response = service.changePassword(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}