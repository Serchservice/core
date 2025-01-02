package com.serch.server.services.transaction.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.services.transaction.requests.FundWalletRequest;
import com.serch.server.services.transaction.requests.WalletUpdateRequest;
import com.serch.server.services.transaction.requests.WithdrawRequest;
import com.serch.server.services.transaction.responses.TransactionGroupResponse;
import com.serch.server.services.transaction.responses.WalletResponse;
import com.serch.server.services.transaction.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
@PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER') || hasRole('USER')")
public class WalletController {
    private final WalletService service;

    @GetMapping
    public ResponseEntity<ApiResponse<WalletResponse>> view() {
        ApiResponse<WalletResponse> response = service.view();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/transactions")
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

    @GetMapping("/fund/verify")
    public ResponseEntity<ApiResponse<WalletResponse>> verify(@RequestParam String reference) {
        ApiResponse<WalletResponse> response = service.verify(reference);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/fund")
    public ResponseEntity<ApiResponse<InitializePaymentData>> fundWallet(@RequestBody FundWalletRequest request) {
        ApiResponse<InitializePaymentData> response = service.fund(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<String>> requestWithdraw(@RequestBody WithdrawRequest request) {
        ApiResponse<String> response = service.withdraw(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateWallet(@RequestBody WalletUpdateRequest request) {
        ApiResponse<String> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}