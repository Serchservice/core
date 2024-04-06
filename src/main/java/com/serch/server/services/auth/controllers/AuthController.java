package com.serch.server.services.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestEmailToken;
import com.serch.server.services.auth.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/email")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<String>> checkIfEmailExists(@RequestParam String email) {
        var response = authService.checkEmail(email);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmailAndToken(@RequestBody RequestEmailToken request) {
        var response = authService.verifyEmailOtp(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
