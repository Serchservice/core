package com.serch.server.services.subscription.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.subscription.requests.InitSubscriptionRequest;
import com.serch.server.services.subscription.requests.VerifySubscriptionRequest;
import com.serch.server.services.subscription.responses.PlanParentResponse;
import com.serch.server.services.subscription.services.PlanService;
import com.serch.server.services.subscription.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanService planService;
    private final SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<ApiResponse<PlanParentResponse>> getPlan(@RequestParam PlanType type) {
        ApiResponse<PlanParentResponse> response = planService.getPlan(type);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PlanParentResponse>>> getPlans() {
        ApiResponse<List<PlanParentResponse>> response = planService.getPlans();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<InitializePaymentData>> subscribe(@RequestBody InitSubscriptionRequest request) {
        ApiResponse<InitializePaymentData> response = subscriptionService.initSubscription(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(@RequestBody VerifySubscriptionRequest request) {
        ApiResponse<String> response = subscriptionService.verifySubscription(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}