package com.serch.server.domains.trip.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.domains.trip.requests.OnlineRequest;
import com.serch.server.domains.trip.responses.ActiveResponse;
import com.serch.server.domains.trip.services.ActiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/active")
public class ActiveController {
    private final ActiveService service;

    @GetMapping("/status")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<ProviderStatus>> status() {
        ApiResponse<ProviderStatus> response = service.fetchStatus();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/status/all")
    @PreAuthorize("hasRole('BUSINESS')")
    public ResponseEntity<ApiResponse<List<ActiveResponse>>> statusAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<ActiveResponse>> response = service.activeList(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/toggle")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<ProviderStatus>> toggle(@RequestBody OnlineRequest request) {
        ApiResponse<ProviderStatus> response = service.toggleStatus(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}