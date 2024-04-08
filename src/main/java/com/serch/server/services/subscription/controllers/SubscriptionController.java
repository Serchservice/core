package com.serch.server.services.subscription.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.subscription.responses.PlanParentResponse;
import com.serch.server.services.subscription.responses.SubscriptionResponse;
import com.serch.server.services.subscription.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account/subscription")
public class SubscriptionController {
    private final SubscriptionService service;

    @GetMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> seeCurrentSubscription() {
        ApiResponse<SubscriptionResponse> response = service.seeCurrentSubscription();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PlanParentResponse>>> getPlans() {
        ApiResponse<List<PlanParentResponse>> response = service.getPlans();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<String>> unsubscribe() {
        ApiResponse<String> response = service.unsubscribe();
        return new ResponseEntity<>(response, response.getStatus());
    }
}
