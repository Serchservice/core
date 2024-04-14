package com.serch.server.services.subscription.services;

/**
 * This is the wrapper class for UpdateSubscription implementation
 *
 * @see UpdateSubscription
 */
public interface UpdateSubscriptionService {
    /**
     * Checks subscriptions to determine if they have expired and need to be updated.
     * If a subscription has expired and has not reached the maximum retry limit, it updates
     * the subscription status and attempts to charge and verify the payment again.
     */
    void checkSubscriptions();
}
