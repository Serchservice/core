package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.subscription.requests.InitSubscriptionRequest;

/**
 * Service interface for initializing subscriptions. It defines methods for initiating new subscriptions
 * and retrieving business sizes associated with subscriptions.
 *
 * @see InitSubscription
 */
public interface InitSubscriptionService {

    /**
     * Initiates a subscription for the given user with the provided subscription request.
     *
     * @param user    The user to subscribe.
     * @param request The subscription request containing plan details.
     * @return An API response containing the payment initialization data.
     * @see User
     * @see InitSubscriptionRequest
     * @see ApiResponse
     * @see InitializePaymentData
     */
    ApiResponse<InitializePaymentData> subscribe(User user, InitSubscriptionRequest request);

    /**
     * Initiates a subscription for a new user based on the provided subscription request.
     *
     * @param request The subscription request containing user and plan details.
     * @return An API response containing the payment initialization data.
     * @see InitSubscriptionRequest
     * @see ApiResponse
     * @see InitializePaymentData
     */
    ApiResponse<InitializePaymentData> subscribe(InitSubscriptionRequest request);

    /**
     * Retrieves the business size associated with a user's subscription.
     *
     * @param user The user's subscription.
     * @return The size of the user's business.
     * @see Subscription
     */
    int getBusinessSize(Subscription user);
}

