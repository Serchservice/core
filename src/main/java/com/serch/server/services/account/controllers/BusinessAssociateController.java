package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.AddAssociateRequest;
import com.serch.server.services.account.services.BusinessAssociateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUSINESS')")
@RequestMapping("/business/associate")
public class BusinessAssociateController {
    private final BusinessAssociateService service;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> add(@RequestBody AddAssociateRequest request) {
        ApiResponse<String> response = service.add(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam UUID id) {
        ApiResponse<String> response = service.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/activate")
    public ResponseEntity<ApiResponse<String>> activate(@RequestParam UUID id) {
        ApiResponse<String> response = service.activate(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivate(@RequestParam UUID id) {
        ApiResponse<String> response = service.deactivate(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
