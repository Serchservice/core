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

/**
 * Controller class for managing user subscription-related operations. It provides endpoints for
 * viewing current subscription details, fetching available plans, and unsubscribing from the current plan.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/account/subscription")
public class SubscriptionController {
    private final SubscriptionService service;

    /**
     * Retrieves details of the current user's subscription.
     *
     * @return A response entity containing details of the current user's subscription.
     * @see SubscriptionResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> seeCurrentSubscription() {
        ApiResponse<SubscriptionResponse> response = service.seeCurrentSubscription();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Retrieves details of all available plans.
     *
     * @return A response entity containing details of all available plans.
     * @see PlanParentResponse
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PlanParentResponse>>> getPlans() {
        ApiResponse<List<PlanParentResponse>> response = service.getPlans();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Unsubscribes the current user from the current subscription plan.
     *
     * @return A response entity indicating the success or failure of the unsubscription process.
     */
    @GetMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<String>> unsubscribe() {
        ApiResponse<String> response = service.unsubscribe();
        return new ResponseEntity<>(response, response.getStatus());
    }
}