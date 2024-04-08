package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.subscription.requests.InitSubscriptionRequest;
import com.serch.server.services.subscription.requests.VerifySubscriptionRequest;
import com.serch.server.services.subscription.responses.PlanParentResponse;
import com.serch.server.services.subscription.responses.SubscriptionResponse;

import java.util.List;

public interface SubscriptionService {
    ApiResponse<InitializePaymentData> initSubscription(InitSubscriptionRequest request);
    ApiResponse<String> verifySubscription(VerifySubscriptionRequest request);
    ApiResponse<SubscriptionResponse> seeCurrentSubscription();
    ApiResponse<List<PlanParentResponse>> getPlans();
    ApiResponse<String> unsubscribe();
    String getAmountFromUserActivePlan(Subscription subscription);
}