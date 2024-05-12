package com.serch.server.services.business.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.AddAssociateRequest;
import com.serch.server.services.business.responses.BusinessAssociateResponse;
import com.serch.server.services.business.services.BusinessAssociateService;
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

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BusinessAssociateResponse>>> all() {
        ApiResponse<List<BusinessAssociateResponse>> response = service.all();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/subscribed")
    public ResponseEntity<ApiResponse<List<BusinessAssociateResponse>>> subscribed() {
        ApiResponse<List<BusinessAssociateResponse>> response = service.subscribed();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/deactivated")
    public ResponseEntity<ApiResponse<List<BusinessAssociateResponse>>> deactivated() {
        ApiResponse<List<BusinessAssociateResponse>> response = service.deactivated();
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
