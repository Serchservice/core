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

/**
 * The WalletController class handles HTTP requests related to wallet operations.
 * It provides endpoints for viewing wallet details, funding wallets, making payments,
 * and updating wallet information.
 *
 * @see WalletService
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
public class WalletController {
    private final WalletService service;

    /**
     * Retrieves details of the user's wallet.
     *
     * @return A ResponseEntity containing an ApiResponse with the wallet details.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<WalletResponse>> view() {
        var response = service.view();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Verifies a fund transaction using the reference provided.
     *
     * @param reference The reference of the fund transaction to be verified.
     * @return A ResponseEntity containing an ApiResponse indicating the result of the verification.
     */
    @GetMapping("/fund/verify")
    public ResponseEntity<ApiResponse<String>> verifyFund(@RequestParam String reference) {
        ApiResponse<String> response = service.verifyFund(reference);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Checks if a user can pay for a trip using the wallet balance.
     *
     * @param trip The trip for which the payment is being checked.
     * @return A ResponseEntity containing an ApiResponse indicating if the user can pay for the trip with the wallet balance.
     */
    @GetMapping("/pay/trip/check")
    public ResponseEntity<ApiResponse<String>> checkIfUserCanPayForTripWithWallet(@RequestParam String trip) {
        ApiResponse<String> response = service.checkIfUserCanPayForTripWithWallet(trip);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Initiates the funding of a wallet with the specified amount.
     *
     * @param request The fund request containing the amount to be funded.
     * @return A ResponseEntity containing an ApiResponse with the payment initialization data.
     */
    @PostMapping("/fund")
    public ResponseEntity<ApiResponse<InitializePaymentData>> fundWallet(@RequestBody FundRequest request) {
        ApiResponse<InitializePaymentData> response = service.fundWallet(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Processes a payment for a subscription using the wallet balance.
     *
     * @param request The payment request for the subscription.
     * @return A ResponseEntity containing an ApiResponse with the result of the payment.
     */
    @PostMapping("/pay/subscription")
    public ResponseEntity<ApiResponse<String>> paySubscription(@RequestBody PayRequest request) {
        ApiResponse<String> response = service.paySubscription(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Processes a payment for a trip using the wallet balance.
     *
     * @param request The payment request for the trip.
     * @return A ResponseEntity containing an ApiResponse with the result of the payment.
     */
    @PostMapping("/pay/trip")
    public ResponseEntity<ApiResponse<String>> payTrip(@RequestBody PayRequest request) {
        ApiResponse<String> response = service.payTrip(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Requests a withdrawal of funds from the wallet.
     *
     * @param request The withdrawal request details.
     * @return A ResponseEntity containing an ApiResponse with the result of the withdrawal request.
     */
    @PostMapping("/pay/withdraw")
    public ResponseEntity<ApiResponse<String>> requestWithdraw(@RequestBody WithdrawRequest request) {
        ApiResponse<String> response = service.requestWithdraw(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Updates the wallet details based on the provided request.
     *
     * @param request The request containing the updated wallet information.
     * @return A ResponseEntity containing an ApiResponse indicating the result of the wallet update operation.
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateWallet(@RequestBody WalletUpdateRequest request) {
        var response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
