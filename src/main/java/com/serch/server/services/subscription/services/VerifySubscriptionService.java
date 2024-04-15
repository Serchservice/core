package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.models.subscription.SubscriptionAuth;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import com.serch.server.services.subscription.requests.VerifySubscriptionRequest;

/**
 * This is a wrapper class for VerifySubscription implementation
 *
 * @see VerifySubscription
 */
public interface VerifySubscriptionService {
    /**
     * Verifies a subscription request for a given user and reference.
     * If the subscription plan is free, it invokes verifyFree method; otherwise, it invokes verifyPaid.
     * @param user The user requesting the subscription verification.
     * @param reference The reference associated with the subscription request.
     * @return An ApiResponse indicating the status of the verification process.
     */
    ApiResponse<String> verify(User user, String reference);
    /**
     * Verifies a subscription request based on the information provided in the VerifySubscriptionRequest.
     * If the subscription plan is free, it invokes verifyFree method; otherwise, it invokes verifyPaid.
     * @param request The VerifySubscriptionRequest containing details of the subscription request.
     * @return An ApiResponse indicating the status of the verification process.
     */
    ApiResponse<String> verify(VerifySubscriptionRequest request);
    /**
     * Creates a subscription authorization entity based on payment verification data.
     * @param emailAddress The email address associated with the subscription.
     * @param response The payment verification data.
     * @return The created SubscriptionAuth entity.
     */
    SubscriptionAuth createAuth(String emailAddress, PaymentVerificationData response);
}
