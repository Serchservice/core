package com.serch.server.services.subscription.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.subscription.requests.InitializeSubscriptionRequest;
import com.serch.server.services.subscription.responses.SubscriptionResponse;
import com.serch.server.services.subscription.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {
    private final SubscriptionService service;

    @GetMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> seeCurrentSubscription() {
        ApiResponse<SubscriptionResponse> response = service.seeCurrentSubscription();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<String>> unsubscribe() {
        ApiResponse<String> response = service.unsubscribe();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<InitializePaymentData>> subscribe(@RequestBody InitializeSubscriptionRequest request) {
        ApiResponse<InitializePaymentData> response = service.subscribe(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(@RequestBody String reference) {
        ApiResponse<String> response = service.verify(reference);
        return new ResponseEntity<>(response, response.getStatus());
    }
}