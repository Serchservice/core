package com.serch.server.services.subscription.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.subscription.requests.InitializeSubscriptionRequest;
import com.serch.server.services.subscription.responses.CurrentSubscriptionResponse;
import com.serch.server.services.subscription.responses.SubscriptionInvoiceResponse;
import com.serch.server.services.subscription.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {
    private final SubscriptionService service;

    @GetMapping
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<CurrentSubscriptionResponse>> seeCurrentSubscription() {
        ApiResponse<CurrentSubscriptionResponse> response = service.seeCurrentSubscription();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/invoices")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<SubscriptionInvoiceResponse>>> invoices() {
        ApiResponse<List<SubscriptionInvoiceResponse>> response = service.invoices();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/unsubscribe")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<String>> unsubscribe() {
        ApiResponse<String> response = service.unsubscribe();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/verify")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<String>> verify(@RequestParam String reference) {
        ApiResponse<String> response = service.verify(reference);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/subscribe")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<InitializePaymentData>> subscribe(@RequestBody InitializeSubscriptionRequest request) {
        ApiResponse<InitializePaymentData> response = service.subscribe(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}