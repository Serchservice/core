package com.serch.server.domains.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.auth.requests.RequestDevice;
import com.serch.server.domains.auth.requests.RequestMFAChallenge;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.auth.responses.MFADataResponse;
import com.serch.server.domains.auth.responses.MFARecoveryCodeResponse;
import com.serch.server.domains.auth.responses.MFAUsageResponse;
import com.serch.server.domains.auth.services.MFAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/mfa")
public class MFAController {
    private final MFAService mfaService;

    @GetMapping("/init")
    public ResponseEntity<ApiResponse<MFADataResponse>> initialize() {
        var response = mfaService.getMFAData();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/usage")
    public ResponseEntity<ApiResponse<MFAUsageResponse>> usage() {
        var response = mfaService.usage();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/recovery/codes")
    public ResponseEntity<ApiResponse<List<MFARecoveryCodeResponse>>> getRecoveryCodes() {
        var response = mfaService.getRecoveryCodes();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/disable")
    public ResponseEntity<ApiResponse<AuthResponse>> disable(@RequestBody RequestDevice device) {
        var response = mfaService.disable(device);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/verify/code")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyCode(@RequestBody RequestMFAChallenge request) {
        var response = mfaService.validateCode(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/recovery/code/verify")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyRecovery(@RequestBody RequestMFAChallenge request) {
        var response = mfaService.validateRecoveryCode(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
