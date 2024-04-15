package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.UpdateProfileRequest;
import com.serch.server.services.account.responses.ProfileResponse;
import com.serch.server.services.account.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService service;

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> profile() {
        ApiResponse<ProfileResponse> response = service.profile();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<String>> update(@RequestBody UpdateProfileRequest request) {
        ApiResponse<String> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
