package com.serch.server.domains.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.account.requests.UpdateE2EKey;
import com.serch.server.domains.account.responses.AccountResponse;
import com.serch.server.domains.account.responses.AdditionalInformationResponse;
import com.serch.server.domains.account.services.AccountDeleteService;
import com.serch.server.domains.account.services.AccountService;
import com.serch.server.domains.account.services.AdditionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
@PreAuthorize(value = "hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER') || hasRole('BUSINESS')")
public class AccountController {
    private final AccountDeleteService deleteService;
    private final AdditionalService additionalService;
    private final AccountService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> accounts() {
        ApiResponse<List<AccountResponse>> response = service.accounts();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete() {
        ApiResponse<String> response = deleteService.delete();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/additional")
    @PreAuthorize(value = "hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<AdditionalInformationResponse>> additional() {
        ApiResponse<AdditionalInformationResponse> response = additionalService.view();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/password")
    public ResponseEntity<ApiResponse<String>> lastPasswordUpdateAt() {
        ApiResponse<String> response = service.lastPasswordUpdateAt();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/timezone")
    public ResponseEntity<ApiResponse<String>> updateTimezone(@RequestParam String zone) {
        ApiResponse<String> response = service.updateTimezone(zone);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/fcm")
    public ResponseEntity<ApiResponse<String>> updateFcmToken(@RequestParam String token) {
        ApiResponse<String> response = service.updateFcmToken(token);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/e2ee")
    public ResponseEntity<ApiResponse<String>> updatePublicEncryptionKey(@RequestBody UpdateE2EKey update) {
        ApiResponse<String> response = service.updatePublicEncryptionKey(update);
        return new ResponseEntity<>(response, response.getStatus());
    }
}