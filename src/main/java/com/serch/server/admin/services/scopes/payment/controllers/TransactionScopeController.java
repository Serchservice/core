package com.serch.server.admin.services.scopes.payment.controllers;

import com.serch.server.admin.services.scopes.payment.responses.transactions.*;
import com.serch.server.admin.services.scopes.payment.services.TransactionScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scope/payment/transaction")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class TransactionScopeController {
    private final TransactionScopeService service;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PaymentApiResponse<TransactionGroupScopeResponse>>>> transactions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<PaymentApiResponse<TransactionGroupScopeResponse>>> response = service.transactions(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PaymentApiResponse<TransactionGroupScopeResponse>>>> search(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String query
    ) {
        ApiResponse<List<PaymentApiResponse<TransactionGroupScopeResponse>>> response = service.search(page, size, query);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<PaymentApiResponse<TransactionGroupScopeResponse>>> filter(
            @RequestParam TransactionStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime end,
            @RequestParam(required = false) TransactionType type
    ) {
        ApiResponse<PaymentApiResponse<TransactionGroupScopeResponse>> response = service.filter(page, size, type, start, end, status);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/resolved")
    public ResponseEntity<ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>>> resolvedTransactions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>> response = service.resolvedTransactions(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/resolved/search")
    public ResponseEntity<ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>>> searchResolved(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String query
    ) {
        ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>> response = service.searchResolved(page, size, query);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/resolved/range")
    public ResponseEntity<ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>>> filterResolved(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime end,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status
    ) {
        ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>> response = service.filterResolved(page, size, start, end, type, status);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<TransactionTypeResponse>>> fetchTypes() {
        ApiResponse<List<TransactionTypeResponse>> response = service.fetchTypes();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TransactionScopeResponse>> transaction(@RequestParam String id) {
        ApiResponse<TransactionScopeResponse> response = service.transaction(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/resolved/find")
    public ResponseEntity<ApiResponse<ResolvedTransactionResponse>> resolved(@RequestParam Long id) {
        ApiResponse<ResolvedTransactionResponse> response = service.resolved(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/resolve")
    public ResponseEntity<ApiResponse<TransactionScopeResponse>> resolve(
            @RequestParam String id,
            @RequestParam TransactionStatus status
    ) {
        ApiResponse<TransactionScopeResponse> response = service.resolve(id, status);
        return new ResponseEntity<>(response, response.getStatus());
    }
}