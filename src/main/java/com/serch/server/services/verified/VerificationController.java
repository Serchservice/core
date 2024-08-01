package com.serch.server.services.verified;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.verified.responses.VerificationResponse;
import com.serch.server.services.verified.services.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/verification")
public class VerificationController {
    private final VerificationService service;

    @GetMapping
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<VerificationResponse>> verification() {
        ApiResponse<VerificationResponse> response = service.verification();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/consent")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<VerificationResponse>> consent() {
        ApiResponse<VerificationResponse> response = service.consent();
        return new ResponseEntity<>(response, response.getStatus());
    }
}