package com.serch.server.services.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/user")
public class UserAuthController {
    private final UserAuthService service;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@RequestBody RequestProfile request) {
        var response = service.signup(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody RequestLogin request) {
        var response = service.login(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/become")
    public ResponseEntity<ApiResponse<AuthResponse>> become(@RequestBody RequestLogin request) {
        var response = service.becomeAUser(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
