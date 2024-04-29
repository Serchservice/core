package com.serch.server.services.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.services.auth.requests.*;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.BusinessAuthService;
import com.serch.server.services.auth.services.ProviderAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/business")
public class BusinessAuthController {
    private final BusinessAuthService authService;
    private final ProviderAuthService providerAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody RequestLogin request) {
        var response = authService.login(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<String>> saveProfile(@RequestBody RequestProviderProfile request) {
        var response = providerAuthService.saveProfile(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/category")
    public ResponseEntity<ApiResponse<String>> saveCategory(@RequestBody RequestSerchCategory request) {
        if(request.getCategory() == null) {
            request.setCategory(SerchCategory.BUSINESS);
        }
        ApiResponse<String> response = providerAuthService.saveCategory(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@RequestBody RequestBusinessProfile profile) {
        var response = authService.signup(profile);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/associate/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> finishAssociateSignup(@RequestBody RequestAuth auth) {
        var response = authService.finishAssociateSignup(auth);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
