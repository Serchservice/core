package com.serch.server.services.shared.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.services.SharedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guest/shared")
public class SharedController {
    private final SharedService service;

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<AccountResponse>> accounts(@RequestParam String id) {
        ApiResponse<AccountResponse> response = service.accounts(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/links")
    public ResponseEntity<ApiResponse<List<GuestResponse>>> links() {
        ApiResponse<List<GuestResponse>> response = service.links();
        return new ResponseEntity<>(response, response.getStatus());
    }
}
