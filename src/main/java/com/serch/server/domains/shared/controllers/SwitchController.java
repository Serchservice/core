package com.serch.server.domains.shared.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.shared.requests.SwitchRequest;
import com.serch.server.domains.shared.responses.GuestResponse;
import com.serch.server.domains.shared.services.SwitchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/switch")
public class SwitchController {
    private final SwitchService service;

    @PostMapping
    public ResponseEntity<ApiResponse<GuestResponse>> switchToGuest(@RequestBody SwitchRequest request) {
        ApiResponse<GuestResponse> response = service.switchToGuest(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/user")
    public ResponseEntity<ApiResponse<AuthResponse>> switchToUser(@RequestBody SwitchRequest request) {
        ApiResponse<AuthResponse> response = service.switchToUser(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}