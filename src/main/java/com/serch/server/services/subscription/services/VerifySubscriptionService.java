package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.subscription.requests.VerifySubscriptionRequest;

public interface VerifySubscriptionService {
    ApiResponse<String> verify(User user, String reference);
    ApiResponse<String> verify(VerifySubscriptionRequest request);
}
