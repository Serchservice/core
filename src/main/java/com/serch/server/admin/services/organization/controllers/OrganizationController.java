package com.serch.server.admin.services.organization.controllers;

import com.serch.server.admin.services.organization.data.OrganizationDto;
import com.serch.server.admin.services.organization.data.OrganizationResponse;
import com.serch.server.admin.services.organization.services.OrganizationService;
import com.serch.server.bases.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organization")
public class OrganizationController {
    private final OrganizationService service;

    @GetMapping
    public ResponseEntity<ApiResponse<OrganizationDto>> find(@RequestParam String secret) {
        ApiResponse<OrganizationDto> response = service.getOrganization(secret);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getAll() {
        ApiResponse<List<OrganizationResponse>> response = service.getAllOrganizations();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> update(@RequestBody @Valid OrganizationDto dto, @RequestParam Long id) {
        ApiResponse<List<OrganizationResponse>> response = service.update(dto, id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> add(@RequestBody @Valid OrganizationDto dto) {
        ApiResponse<List<OrganizationResponse>> response = service.add(dto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> delete(@PathVariable Long id) {
        ApiResponse<List<OrganizationResponse>> response = service.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}