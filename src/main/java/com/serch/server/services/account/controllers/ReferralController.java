package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.ReferralResponse;
import com.serch.server.services.account.services.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/referral")
public class ReferralController {
    private final ReferralService service;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ReferralResponse>>> viewReferrals() {
        var response = service.viewReferrals();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<String>> verifyLink(@RequestParam String link) {
        var response = service.verifyLink(link);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
