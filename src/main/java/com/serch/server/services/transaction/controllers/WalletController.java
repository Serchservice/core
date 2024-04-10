package com.serch.server.services.transaction.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.transaction.requests.FundRequest;
import com.serch.server.services.transaction.requests.PayRequest;
import com.serch.server.services.transaction.requests.WalletUpdateRequest;
import com.serch.server.services.transaction.requests.WithdrawRequest;
import com.serch.server.services.transaction.responses.WalletResponse;
import com.serch.server.services.transaction.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
public class WalletController {
    private final WalletService service;

    @GetMapping
    public ResponseEntity<ApiResponse<WalletResponse>> view() {
        var response = service.view();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/fund/verify")
    public ResponseEntity<ApiResponse<String>> verifyFund(@RequestParam String reference) {
        ApiResponse<String> response = service.verifyFund(reference);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/pay/trip/check")
    public ResponseEntity<ApiResponse<String>> checkIfUserCanPayForTripWithWallet(@RequestParam String trip) {
        ApiResponse<String> response = service.checkIfUserCanPayForTripWithWallet(trip);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/fund")
    public ResponseEntity<ApiResponse<InitializePaymentData>> fundWallet(@RequestBody FundRequest request) {
        ApiResponse<InitializePaymentData> response = service.fundWallet(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/pay/subscription")
    public ResponseEntity<ApiResponse<String>> paySubscription(@RequestBody PayRequest request) {
        ApiResponse<String> response = service.paySubscription(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/pay/trip")
    public ResponseEntity<ApiResponse<String>> payTrip(@RequestBody PayRequest request) {
        ApiResponse<String> response = service.payTrip(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/pay/withdraw")
    public ResponseEntity<ApiResponse<String>> requestWithdraw(@RequestBody WithdrawRequest request) {
        ApiResponse<String> response = service.requestWithdraw(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateWallet(@RequestBody WalletUpdateRequest request) {
        var response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
