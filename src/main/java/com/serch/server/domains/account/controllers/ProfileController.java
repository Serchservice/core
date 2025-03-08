package com.serch.server.domains.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.account.requests.UpdateProfileRequest;
import com.serch.server.domains.account.responses.ProfileResponse;
import com.serch.server.domains.account.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@PreAuthorize(value = "hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
public class ProfileController {
    private final ProfileService service;

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> profile() {
        ApiResponse<ProfileResponse> response = service.profile();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping(value = "/update")
    public ResponseEntity<ApiResponse<ProfileResponse>> update(@RequestBody UpdateProfileRequest request) {
        ApiResponse<ProfileResponse> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}