package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.UpdateProfileRequest;
import com.serch.server.services.account.responses.BusinessProfileResponse;
import com.serch.server.services.account.responses.ProfileResponse;
import com.serch.server.services.account.services.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business/profile")
public class BusinessController {
    private final BusinessService service;

    @GetMapping
    public ResponseEntity<ApiResponse<BusinessProfileResponse>> profile() {
        ApiResponse<BusinessProfileResponse> response = service.profile();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/associates")
    public ResponseEntity<ApiResponse<List<ProfileResponse>>> associates() {
        ApiResponse<List<ProfileResponse>> response = service.associates();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/associates/subscribed")
    public ResponseEntity<ApiResponse<List<ProfileResponse>>> subscribedAssociates() {
        ApiResponse<List<ProfileResponse>> response = service.subscribedAssociates();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<String>> update(@RequestBody UpdateProfileRequest request) {
        ApiResponse<String> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
