package com.serch.server.services.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.responses.SessionResponse;
import com.serch.server.services.auth.services.SessionService;
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
    public ResponseEntity<ApiResponse<SessionResponse>> refreshToken(@RequestParam String accessToken) {
        var response = sessionService.refreshSession(accessToken);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
