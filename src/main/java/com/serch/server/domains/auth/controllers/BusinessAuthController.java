package com.serch.server.domains.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.domains.auth.requests.RequestBusinessProfile;
import com.serch.server.domains.auth.requests.RequestLogin;
import com.serch.server.domains.auth.requests.RequestProviderProfile;
import com.serch.server.domains.auth.requests.RequestSerchCategory;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.auth.responses.PendingRegistrationResponse;
import com.serch.server.domains.auth.services.AuthService;
import com.serch.server.domains.auth.services.BusinessAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/business")
public class BusinessAuthController {
    private final BusinessAuthService businessAuthService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody RequestLogin request) {
        var response = businessAuthService.login(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<PendingRegistrationResponse>> saveProfile(@RequestBody RequestProviderProfile request) {
        ApiResponse<PendingRegistrationResponse> response = authService.saveProfile(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/category")
    public ResponseEntity<ApiResponse<PendingRegistrationResponse>> saveCategory(@RequestBody RequestSerchCategory request) {
        if(request.getCategory() == null) {
            request.setCategory(SerchCategory.BUSINESS);
        }

        ApiResponse<PendingRegistrationResponse> response = authService.saveCategory(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@RequestBody RequestBusinessProfile profile) {
        var response = businessAuthService.signup(profile);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
