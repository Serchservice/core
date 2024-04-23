package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.Gender;
import com.serch.server.services.account.responses.AccountSettingResponse;
import com.serch.server.services.account.services.AccountSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account/settings")
public class AccountSettingController {
    private final AccountSettingService service;

    @GetMapping
    public ResponseEntity<ApiResponse<AccountSettingResponse>> settings() {
        ApiResponse<AccountSettingResponse> response = service.settings();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/change/trip/gender")
    public ResponseEntity<ApiResponse<String>> setGenderForTrip(@RequestParam Gender gender) {
        ApiResponse<String> response = service.setGenderForTrip(gender);
        return new ResponseEntity<>(response, response.getStatus());
    }
}