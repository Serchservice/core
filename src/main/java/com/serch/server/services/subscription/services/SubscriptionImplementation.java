package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.subscription.requests.InitSubscriptionRequest;
import com.serch.server.services.subscription.requests.VerifySubscriptionRequest;
import com.serch.server.services.subscription.responses.PlanParentResponse;
import com.serch.server.services.subscription.responses.SubscriptionResponse;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionImplementation implements SubscriptionService {
    private final InitSubscriptionService initService;
    private final VerifySubscriptionService verifyService;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<InitializePaymentData> initSubscription(InitSubscriptionRequest request) {
        Optional<User> loggedInUser = userRepository.findByEmailAddress(UserUtil.getLoginUser());

        if(loggedInUser.isPresent()) {
            return initService.subscribe(loggedInUser.get(), request);
        } else {
            return initService.subscribe(request);
        }
    }

    @Override
    public ApiResponse<String> verifySubscription(VerifySubscriptionRequest request) {
        Optional<User> loggedInUser = userRepository.findByEmailAddress(UserUtil.getLoginUser());

        if(loggedInUser.isPresent()) {
            return verifyService.verify(loggedInUser.get(), request.getReference());
        } else {
            return verifyService.verify(request);
        }
    }

    @Override
    public void checkSubscriptions() {

    }

    @Override
    public ApiResponse<SubscriptionResponse> getCurrentSubscription() {
        return null;
    }

    @Override
    public ApiResponse<List<PlanParentResponse>> getPlans() {
        return null;
    }

    @Override
    public ApiResponse<String> unsubscribe() {
        return null;
    }
}
