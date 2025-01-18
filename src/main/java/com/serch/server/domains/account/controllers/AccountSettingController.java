package com.serch.server.domains.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.account.responses.AccountSettingResponse;
import com.serch.server.domains.account.services.AccountSettingService;
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

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<AccountSettingResponse>> update(@RequestBody AccountSettingResponse request) {
        ApiResponse<AccountSettingResponse> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}