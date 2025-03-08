package com.serch.server.admin.services.scopes.payment.controllers;

import com.serch.server.admin.services.scopes.payment.responses.transactions.PaymentApiResponse;
import com.serch.server.admin.services.scopes.payment.responses.wallet.WalletScopeResponse;
import com.serch.server.admin.services.scopes.payment.services.WalletScopeService;
import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scope/payment/wallet")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class WalletScopeController {
    private final WalletScopeService service;

    @GetMapping("all")
    public ResponseEntity<ApiResponse<PaymentApiResponse<WalletScopeResponse>>> wallets(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<PaymentApiResponse<WalletScopeResponse>> response = service.wallets(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<WalletScopeResponse>> wallet(@RequestParam String id) {
        ApiResponse<WalletScopeResponse> response = service.wallet(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
