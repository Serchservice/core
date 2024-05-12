package com.serch.server.services.business.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.business.responses.BusinessAssociateResponse;
import com.serch.server.services.business.services.BusinessSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business/subscription")
@PreAuthorize("hasRole('BUSINESS')")
public class BusinessSubscriptionController {
    private final BusinessSubscriptionService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BusinessAssociateResponse>>> subscriptions() {
        ApiResponse<List<BusinessAssociateResponse>> response = service.subscriptions();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/add/{id}")
    public ResponseEntity<ApiResponse<List<BusinessAssociateResponse>>> add(@PathVariable("id") UUID id) {
        ApiResponse<List<BusinessAssociateResponse>> response = service.add(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/add/all")
    public ResponseEntity<ApiResponse<List<BusinessAssociateResponse>>> addAll(@RequestBody List<UUID> ids) {
        ApiResponse<List<BusinessAssociateResponse>> response = service.addAll(ids);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/suspend/{id}")
    public ResponseEntity<ApiResponse<List<BusinessAssociateResponse>>> suspend(@PathVariable("id") UUID id) {
        ApiResponse<List<BusinessAssociateResponse>> response = service.suspend(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<ApiResponse<List<BusinessAssociateResponse>>> remove(@PathVariable("id") UUID id) {
        ApiResponse<List<BusinessAssociateResponse>> response = service.remove(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}