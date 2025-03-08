package com.serch.server.domains.transaction.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.transaction.responses.TransactionGroupResponse;
import com.serch.server.domains.transaction.services.TransactionResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallet/transaction")
@PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER') || hasRole('USER')")
public class TransactionController {
    private final TransactionResponseService service;

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TransactionGroupResponse>>> transactions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<TransactionGroupResponse>> response = service.transactions(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<TransactionGroupResponse>>> recent() {
        ApiResponse<List<TransactionGroupResponse>> response = service.recent();
        return new ResponseEntity<>(response, response.getStatus());
    }
}