package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.subscription.requests.InitSubscriptionRequest;
import com.serch.server.services.subscription.requests.VerifySubscriptionRequest;
import com.serch.server.services.subscription.responses.PlanParentResponse;
import com.serch.server.services.subscription.responses.SubscriptionResponse;

import java.util.List;

/**
 * This is the wrapper class for SubscriptionImplementation.
 *
 * @see SubscriptionImplementation
 */
public interface SubscriptionService {
    /**
     * Initializes a new subscription based on the provided request.
     * If the user is logged in, it subscribes the logged-in user.
     * Otherwise, it subscribes using the provided request data.
     * @param request The request containing subscription initialization data.
     * @return An API response containing the initialized payment data.
     */
    ApiResponse<InitializePaymentData> initSubscription(InitSubscriptionRequest request);
    /**
     * Verifies a subscription based on the provided verification request.
     * If the user is logged in, it verifies the subscription for the logged-in user.
     * Otherwise, it verifies using the provided verification request data.
     * @param request The verification request containing the reference to verify.
     * @return An API response indicating the result of the subscription verification.
     */
    ApiResponse<String> verifySubscription(VerifySubscriptionRequest request);
    /**
     * Retrieves details of the current subscription of the logged-in user.
     * @return An API response containing details of the current subscription.
     */
    ApiResponse<SubscriptionResponse> seeCurrentSubscription();
    /**
     * Retrieves the available subscription plans based on the user's current subscription status.
     * @return An API response containing a list of available subscription plans.
     */
    ApiResponse<List<PlanParentResponse>> getPlans();
    /**
     * Unsubscribes the logged-in user from the current subscription.
     * @return An API response indicating the result of the un-subscription process.
     */
    ApiResponse<String> unsubscribe();

    /**
     * Retrieves the amount the user is currently spending on its current plan
     * @param subscription The user's active subscription
     *
     * @return String of amount
     */
    String getAmountFromUserActivePlan(Subscription subscription);
}