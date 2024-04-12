package com.serch.server.services.shared.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.shared.requests.CreateGuestRequest;
import com.serch.server.services.shared.requests.VerifyEmailRequest;
import com.serch.server.services.shared.responses.GuestAuthResponse;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.services.GuestAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guest/auth")
public class GuestAuthController {
    private final GuestAuthService service;

    @GetMapping("/link/verify")
    public ResponseEntity<ApiResponse<GuestAuthResponse>> verifyLink(@RequestParam String link) {
        ApiResponse<GuestAuthResponse> response = service.verifyLink(link);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/email/ask")
    public ResponseEntity<ApiResponse<String>> askToVerifyEmail(@RequestBody VerifyEmailRequest request) {
        ApiResponse<String> response = service.askToVerifyEmail(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmailWithToken(@RequestBody VerifyEmailRequest request) {
        ApiResponse<String> response = service.verifyEmailWithToken(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/email/existing/ask")
    public ResponseEntity<ApiResponse<String>> askToConfirmExistingEmailIdentity(@RequestBody VerifyEmailRequest request) {
        ApiResponse<String> response = service.askToConfirmExistingEmailIdentity(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/email/existing/verify")
    public ResponseEntity<ApiResponse<String>> confirmExistingEmailIdentityWithToken(@RequestBody VerifyEmailRequest request) {
        ApiResponse<String> response = service.confirmExistingEmailIdentityWithToken(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GuestResponse>> create(@RequestBody CreateGuestRequest request) {
        ApiResponse<GuestResponse> response = service.create(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create/existing")
    public ResponseEntity<ApiResponse<GuestResponse>> createFromExistingUser(@RequestBody CreateGuestRequest request) {
        ApiResponse<GuestResponse> response = service.createFromExistingUser(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create/add")
    public ResponseEntity<ApiResponse<GuestResponse>> addGuestAccountToLink(@RequestBody CreateGuestRequest request) {
        ApiResponse<GuestResponse> response = service.addGuestAccountToLink(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<GuestResponse>> login(@RequestBody VerifyEmailRequest request) {
        ApiResponse<GuestResponse> response = service.login(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
