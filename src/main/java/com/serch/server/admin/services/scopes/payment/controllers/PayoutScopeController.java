package com.serch.server.admin.services.scopes.payment.controllers;

import com.serch.server.admin.services.scopes.payment.responses.payouts.PayoutScopeResponse;
import com.serch.server.admin.services.scopes.payment.responses.payouts.PayoutResponse;
import com.serch.server.admin.services.scopes.payment.responses.transactions.PaymentApiResponse;
import com.serch.server.admin.services.scopes.payment.services.PayoutScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scope/payment/payout")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class PayoutScopeController {
    private final PayoutScopeService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PayoutResponse>> find(@RequestParam String id) {
        ApiResponse<PayoutResponse> response = service.find(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>>> payouts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) TransactionStatus status
            ) {
        ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>> response = service.payouts(page, size, status);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/pay")
    public ResponseEntity<ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>>> payout(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam List<String> id
    ) {
        ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>> response = service.payout(page, size, id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/cancel")
    public ResponseEntity<ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>>> cancel(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam List<String> id
    ) {
        ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>> response = service.cancel(page, size, id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/pay/batch")
    public ResponseEntity<ApiResponse<PayoutResponse>> payout(@RequestParam String id) {
        ApiResponse<PayoutResponse> response = service.payout(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/cancel/batch")
    public ResponseEntity<ApiResponse<PayoutResponse>> cancel(@RequestParam String id) {
        ApiResponse<PayoutResponse> response = service.cancel(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}