package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.subscription.requests.InitSubscriptionRequest;

public interface InitSubscriptionService {
    ApiResponse<InitializePaymentData> subscribe(User user, InitSubscriptionRequest request);
    ApiResponse<InitializePaymentData> subscribe(InitSubscriptionRequest request);
    int getBusinessSize(Subscription user);
}
