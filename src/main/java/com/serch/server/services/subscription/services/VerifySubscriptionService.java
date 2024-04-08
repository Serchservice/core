package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.models.subscription.SubscriptionAuth;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import com.serch.server.services.subscription.requests.VerifySubscriptionRequest;

public interface VerifySubscriptionService {
    ApiResponse<String> verify(User user, String reference);
    ApiResponse<String> verify(VerifySubscriptionRequest request);
    SubscriptionAuth createAuth(String emailAddress, PaymentVerificationData response);
}
