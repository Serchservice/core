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

/**
 * Controller class for handling plan-related operations. It provides endpoints for retrieving plans,
 * subscribing to plans, and verifying subscriptions.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanService planService;
    private final SubscriptionService subscriptionService;

    /**
     * Retrieves details of a specific plan based on the provided plan type.
     *
     * @param type The type of plan to retrieve.
     * @return A response entity containing the details of the requested plan.
     * @see PlanType
     * @see PlanParentResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PlanParentResponse>> getPlan(@RequestParam PlanType type) {
        ApiResponse<PlanParentResponse> response = planService.getPlan(type);
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
        ApiResponse<List<PlanParentResponse>> response = planService.getPlans();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Subscribes a user to a plan based on the provided subscription request.
     *
     * @param request The subscription request containing user and plan details.
     * @return A response entity containing payment initialization data for the subscription.
     * @see InitSubscriptionRequest
     * @see InitializePaymentData
     */
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<InitializePaymentData>> subscribe(@RequestBody InitSubscriptionRequest request) {
        ApiResponse<InitializePaymentData> response = subscriptionService.initSubscription(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Verifies a subscription based on the provided verification request.
     *
     * @param request The verification request containing subscription reference details.
     * @return A response entity containing the verification status.
     * @see VerifySubscriptionRequest
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(@RequestBody VerifySubscriptionRequest request) {
        ApiResponse<String> response = subscriptionService.verifySubscription(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}