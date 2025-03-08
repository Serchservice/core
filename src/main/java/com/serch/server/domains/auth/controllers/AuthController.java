package com.serch.server.domains.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.auth.requests.RequestEmailToken;
import com.serch.server.domains.auth.requests.RequestLogin;
import com.serch.server.domains.auth.responses.PendingRegistrationResponse;
import com.serch.server.domains.auth.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/email/check")
    public ResponseEntity<ApiResponse<String>> checkIfEmailExists(@RequestParam String email) {
        ApiResponse<String> response = authService.checkEmail(email);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmailAndToken(@RequestBody RequestEmailToken request) {
        ApiResponse<String> response = authService.verifyEmailOtp(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/email/verify/pending")
    public ResponseEntity<ApiResponse<PendingRegistrationResponse>> verifyPending(@RequestBody RequestLogin request) {
        ApiResponse<PendingRegistrationResponse> response = authService.verifyPendingRegistration(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}