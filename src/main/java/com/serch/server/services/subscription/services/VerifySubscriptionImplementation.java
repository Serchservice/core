package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.models.auth.User;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.models.subscription.SubscriptionRequest;
import com.serch.server.repositories.subscription.SubscriptionRepository;
import com.serch.server.services.payment.responses.PaymentVerificationResponse;
import com.serch.server.services.subscription.requests.VerifySubscriptionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerifySubscriptionImplementation implements VerifySubscriptionService {
    private final PaymentService paymentService;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public ApiResponse<String> verify(User user, String reference) {
        return null;
    }

    @Override
    public ApiResponse<String> verify(VerifySubscriptionRequest request) {
        return null;
    }

    private void verifyPaid(SubscriptionRequest request, Optional<User> user) {
        PaymentVerificationResponse response = paymentService.verify(request.getReference());

        if(response.getData().getStatus().equalsIgnoreCase("success")) {
            if(user.isPresent()) {
                Optional<Subscription> subscription = subscriptionRepository.findByUser_Id(user.get().getId());

                SubscriptionAuth
                if(subscription.isPresent()) {
                    subscription.get().setPlan(request.getParent());
                    subscription.get().setChild(request.getChild());
                    subscription.get().setPlanStatus(PlanStatus.ACTIVE);
                    subscription.get().setAuth();
                }
            } else {
                return ;'
            }
        }
    }
}
