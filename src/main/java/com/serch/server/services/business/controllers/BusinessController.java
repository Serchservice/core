package com.serch.server.services.business.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.business.requests.UpdateBusinessRequest;
import com.serch.server.services.business.responses.BusinessProfileResponse;
import com.serch.server.services.business.services.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile/business")
@PreAuthorize(value = "hasRole('BUSINESS')")
public class BusinessController {
    private final BusinessService service;

    @GetMapping
    public ResponseEntity<ApiResponse<BusinessProfileResponse>> profile() {
        ApiResponse<BusinessProfileResponse> response = service.profile();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<BusinessProfileResponse>> update(@RequestBody UpdateBusinessRequest request) {
        ApiResponse<BusinessProfileResponse> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}