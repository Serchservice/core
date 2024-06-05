package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import com.serch.server.services.subscription.requests.InitializeSubscriptionRequest;
import com.serch.server.services.subscription.responses.CurrentSubscriptionResponse;
import com.serch.server.services.subscription.responses.SubscriptionInvoiceResponse;
import com.serch.server.services.subscription.services.implementations.SubscriptionImplementation;

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
     *
     * @param request The request containing subscription initialization data.
     * @return An API response containing the initialized payment data.
     *
     * @see InitializePaymentData
     * @see InitializeSubscriptionRequest
     */
    ApiResponse<InitializePaymentData> subscribe(InitializeSubscriptionRequest request);

    /**
     * Verifies a subscription based on the provided verification request.
     * If the user is logged in, it verifies the subscription for the logged-in user.
     * Otherwise, it verifies using the provided verification request data.
     *
     * @param reference The verification reference.
     * @return An API response indicating the result of the subscription verification.
     */
    ApiResponse<String> verify(String reference);

    /**
     * Retrieves details of the current subscription of the logged-in user.
     * @return An API response containing details of the current subscription.
     */
    ApiResponse<CurrentSubscriptionResponse> seeCurrentSubscription();

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
    String getActiveAmount(Subscription subscription);

    /**
     * Create a new authentication from the payment verification data
     *
     * @param subscription The subscription to attach the auth to.
     * @param data The PaymentVerification
     *
     * @see Subscription
     * @see PaymentVerificationData
     */
    void createAuth(Subscription subscription, PaymentVerificationData data);

    /**
     * Checks if the logged-in user has subscription active (If the account should subscribe)
     * before performing any action
     */
    void checkSubscription();

    /**
     * Creates a subscription invoice based on the subscription details.
     * @param subscription The subscription for which the invoice is created.
     * @param associates The list of associates tied to the subscription
     *
     * @see Subscription
     */
    void createInvoice(Subscription subscription, List<Profile> associates);

    /**
     * This fetches the list of subscription invoices
     *
     * @return {@link ApiResponse} list of {@link SubscriptionInvoiceResponse}
     */
    ApiResponse<List<SubscriptionInvoiceResponse>> invoices();
}