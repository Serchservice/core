package com.serch.server.services.shared.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.shared.requests.GuestToUserRequest;
import com.serch.server.services.shared.responses.GuestActivityResponse;
import com.serch.server.services.shared.services.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guest")
public class GuestController {
    private final GuestService service;

    @GetMapping("/activity")
    public ResponseEntity<ApiResponse<GuestActivityResponse>> activity(@RequestParam String linkId, @RequestParam String guestId) {
        ApiResponse<GuestActivityResponse> response = service.activity(guestId, linkId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/user/become")
    public ResponseEntity<ApiResponse<AuthResponse>> becomeAUser(@RequestBody GuestToUserRequest request) {
        ApiResponse<AuthResponse> response = service.becomeAUser(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}