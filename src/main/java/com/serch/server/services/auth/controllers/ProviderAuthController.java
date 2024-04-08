package com.serch.server.services.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.*;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.ProviderAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/provider")
public class ProviderAuthController {
    private final ProviderAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody RequestLogin request) {
        var response = authService.login(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<String>> saveProfile(@RequestBody RequestProviderProfile request) {
        var response = authService.saveProfile(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/category")
    public ResponseEntity<ApiResponse<String>> saveCategory(@RequestBody RequestSerchCategory request) {
        var response = authService.saveCategory(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/specialties")
    public ResponseEntity<ApiResponse<String>> saveSpecialties(@RequestBody RequestAuthSpecialty request) {
        var response = authService.saveSpecialties(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/additional")
    public ResponseEntity<ApiResponse<String>> saveAdditional(@RequestBody RequestAdditionalInformation request) {
        var response = authService.saveAdditional(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> savePlan(@RequestBody RequestAuth auth) {
        var response = authService.finishSignup(auth);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
