package com.serch.server.domains.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.account.services.SpecialtyService;
import com.serch.server.domains.account.responses.SpecialtyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/specialty")
public class SpecialtyController {
    private final SpecialtyService service;

    @GetMapping("/search")
    @PreAuthorize(value = "hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER') || hasRole('USER')")
    public ResponseEntity<ApiResponse<List<SpecialtyResponse>>> search(
            @RequestParam String query,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<SpecialtyResponse>> response = service.search(query, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<String>>> specialties(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<String>> response = service.specialties(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/add")
    @PreAuthorize(value = "hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> add(@RequestParam String specialty) {
        ApiResponse<SpecialtyResponse> response = service.add(specialty);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/delete")
    @PreAuthorize(value = "hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam Long id) {
        ApiResponse<String> response = service.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}