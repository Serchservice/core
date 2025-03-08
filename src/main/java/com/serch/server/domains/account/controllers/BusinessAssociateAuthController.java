package com.serch.server.domains.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.account.requests.AssociateInviteRequest;
import com.serch.server.domains.account.responses.VerifiedInviteResponse;
import com.serch.server.domains.account.services.BusinessAssociateAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/associate")
public class BusinessAssociateAuthController {
    private final BusinessAssociateAuthService service;

    @GetMapping
    public ResponseEntity<ApiResponse<VerifiedInviteResponse>> verifyLink(@RequestParam String token) {
        ApiResponse<VerifiedInviteResponse> response = service.verifyLink(token);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuthResponse>> changePassword(@RequestBody AssociateInviteRequest request) {
        ApiResponse<AuthResponse> response = service.acceptInvite(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
