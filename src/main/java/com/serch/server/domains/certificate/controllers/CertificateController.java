package com.serch.server.domains.certificate.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.certificate.responses.CertificateData;
import com.serch.server.domains.certificate.responses.CertificateResponse;
import com.serch.server.domains.certificate.responses.VerifyCertificateResponse;
import com.serch.server.domains.certificate.services.CertificateService;
import com.serch.server.core.file.requests.FileUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/certificate")
public class CertificateController {
    private final CertificateService service;

    @GetMapping
    @PreAuthorize(value = "hasRole('BUSINESS') || hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<CertificateResponse>> fetch() {
        ApiResponse<CertificateResponse> response = service.fetch();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/generate")
    @PreAuthorize(value = "hasRole('BUSINESS') || hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<CertificateData>> generate() {
        ApiResponse<CertificateData> response = service.generate();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/upload")
    @PreAuthorize(value = "hasRole('BUSINESS') || hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<CertificateResponse>> upload(@RequestBody FileUploadRequest request) {
        ApiResponse<CertificateResponse> response = service.upload(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyCertificateResponse>> verify(@RequestParam String token) {
        ApiResponse<VerifyCertificateResponse> response = service.verify(token);
        return new ResponseEntity<>(response, response.getStatus());
    }
}