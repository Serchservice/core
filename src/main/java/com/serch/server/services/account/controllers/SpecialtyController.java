package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.services.SpecialtyService;
import com.serch.server.services.company.responses.SpecialtyKeywordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/specialty")
@PreAuthorize(value = "hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
public class SpecialtyController {
    private final SpecialtyService service;

    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<List<SpecialtyKeywordResponse>>> specials() {
        ApiResponse<List<SpecialtyKeywordResponse>> response = service.specials();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/add")
    public ResponseEntity<ApiResponse<SpecialtyKeywordResponse>> add(@RequestParam Long id) {
        ApiResponse<SpecialtyKeywordResponse> response = service.add(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam Long id) {
        ApiResponse<String> response = service.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}