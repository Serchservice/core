package com.serch.server.domains.nearby.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.account.requests.GoAccountUpdateRequest;
import com.serch.server.domains.nearby.services.account.responses.GoAccountResponse;
import com.serch.server.domains.nearby.services.account.services.GoAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/go/account")
public class GoAccountController {
    private final GoAccountService service;

    @GetMapping
    public ResponseEntity<ApiResponse<GoAccountResponse>> get() {
        ApiResponse<GoAccountResponse> response = service.get();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<GoAccountResponse>> update(@RequestBody GoAccountUpdateRequest request) {
        ApiResponse<GoAccountResponse> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}