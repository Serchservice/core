package com.serch.server.admin.services.scopes.support.controllers;

import com.serch.server.admin.services.scopes.support.responses.ComplaintScopeResponse;
import com.serch.server.admin.services.scopes.support.services.ComplaintScopeService;
import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scope/support/complaint")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class ComplaintScopeController {
    private final ComplaintScopeService service;

    @GetMapping
    public ResponseEntity<ApiResponse<ComplaintScopeResponse>> complaint(@RequestParam String emailAddress) {
        ApiResponse<ComplaintScopeResponse> response = service.complaint(emailAddress);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ComplaintScopeResponse>>> complaints() {
        ApiResponse<List<ComplaintScopeResponse>> response = service.complaints();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/resolve")
    public ResponseEntity<ApiResponse<List<ComplaintScopeResponse>>> resolve(@RequestParam String id) {
        ApiResponse<List<ComplaintScopeResponse>> response = service.resolve(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
