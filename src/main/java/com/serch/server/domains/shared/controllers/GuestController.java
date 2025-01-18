package com.serch.server.domains.shared.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.shared.requests.GuestToUserRequest;
import com.serch.server.domains.shared.requests.SwitchRequest;
import com.serch.server.domains.shared.services.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guest")
public class GuestController {
    private final GuestService service;

    @GetMapping("/user/become")
    public ResponseEntity<ApiResponse<AuthResponse>> becomeAUser(@RequestBody GuestToUserRequest request) {
        ApiResponse<AuthResponse> response = service.becomeAUser(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/fcm")
    public ResponseEntity<ApiResponse<String>> updateFcmToken(@RequestParam String token, @RequestParam String guest) {
        ApiResponse<String> response = service.updateFcmToken(token, guest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/timezone")
    public ResponseEntity<ApiResponse<String>> updateTimezone(@RequestParam String zone, @RequestParam String guest) {
        ApiResponse<String> response = service.updateTimezone(zone, guest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @MessageMapping("/guest/refresh")
    void refresh(@Payload SwitchRequest request) {
        service.refresh(request);
    }
}