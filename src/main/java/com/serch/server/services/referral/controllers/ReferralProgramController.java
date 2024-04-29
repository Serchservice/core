package com.serch.server.services.referral.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.referral.responses.ReferralProgramResponse;
import com.serch.server.services.referral.services.ReferralProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/referral/program")
public class ReferralProgramController {
    private final ReferralProgramService service;

    @GetMapping
    public ResponseEntity<ApiResponse<ReferralProgramResponse>> program() {
        ApiResponse<ReferralProgramResponse> response = service.program();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/verify/link")
    public ResponseEntity<ApiResponse<ReferralProgramResponse>> verifyLink(@RequestParam String link) {
        ApiResponse<ReferralProgramResponse> response = service.verifyLink(link);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/verify/code")
    public ResponseEntity<ApiResponse<ReferralProgramResponse>> verifyCode(@RequestParam String code) {
        ApiResponse<ReferralProgramResponse> response = service.verifyCode(code);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
