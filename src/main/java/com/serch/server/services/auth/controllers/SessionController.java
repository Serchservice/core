package com.serch.server.services.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.responses.SessionResponse;
import com.serch.server.core.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/session")
public class SessionController {
    private final SessionService sessionService;

    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<SessionResponse>> refreshSession(@RequestParam String token) {
        ApiResponse<SessionResponse> response = sessionService.refreshSession(token);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateSession(
            @RequestParam String token,
            @RequestParam String country,
            @RequestParam String state
    ) {
        ApiResponse<String> response = sessionService.validateSession(token, state, country);
        return new ResponseEntity<>(response, response.getStatus());
    }
}