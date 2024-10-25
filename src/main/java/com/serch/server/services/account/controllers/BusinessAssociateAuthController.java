package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.account.requests.AssociateInviteRequest;
import com.serch.server.services.account.responses.VerifiedInviteResponse;
import com.serch.server.services.account.services.BusinessAssociateAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/associate")
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
