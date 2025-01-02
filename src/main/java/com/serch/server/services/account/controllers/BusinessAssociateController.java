package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.AddAssociateRequest;
import com.serch.server.services.account.responses.BusinessAssociateResponse;
import com.serch.server.services.account.services.BusinessAssociateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUSINESS')")
@RequestMapping("/business/associate")
public class BusinessAssociateController {
    private final BusinessAssociateService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BusinessAssociateResponse>>> all(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<BusinessAssociateResponse>> response = service.all(q, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/resend")
    public ResponseEntity<ApiResponse<String>> add(@RequestParam UUID id) {
        ApiResponse<String> response = service.resendInvite(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<List<BusinessAssociateResponse>>> add(@RequestBody AddAssociateRequest request) {
        ApiResponse<List<BusinessAssociateResponse>> response = service.add(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<List<BusinessAssociateResponse>>> delete(@RequestParam UUID id) {
        ApiResponse<List<BusinessAssociateResponse>> response = service.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/activate")
    public ResponseEntity<ApiResponse<BusinessAssociateResponse>> activate(@RequestParam UUID id) {
        ApiResponse<BusinessAssociateResponse> response = service.activate(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<ApiResponse<BusinessAssociateResponse>> deactivate(@RequestParam UUID id) {
        ApiResponse<BusinessAssociateResponse> response = service.deactivate(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
