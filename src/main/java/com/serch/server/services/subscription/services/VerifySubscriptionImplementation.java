package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.models.auth.User;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.models.subscription.SubscriptionAuth;
import com.serch.server.models.subscription.SubscriptionRequest;
import com.serch.server.repositories.subscription.SubscriptionAuthRepository;
import com.serch.server.repositories.subscription.SubscriptionRepository;
import com.serch.server.services.payment.core.PaymentService;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import com.serch.server.services.subscription.requests.VerifySubscriptionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerifySubscriptionImplementation implements VerifySubscriptionService {
    private final PaymentService paymentService;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionAuthRepository subscriptionAuthRepository;

    @Override
    public ApiResponse<String> verify(User user, String reference) {
        return null;
    }

    @Override
    public ApiResponse<String> verify(VerifySubscriptionRequest request) {
        return null;
    }

    private void verifyPaid(SubscriptionRequest request, Optional<User> user) {
        PaymentVerificationData response = paymentService.verify(request.getReference());

        if(user.isPresent()) {
            Optional<Subscription> existing = subscriptionRepository.findByUser_Id(user.get().getId());

            if(existing.isPresent()) {
                existing.get().setPlan(request.getParent());
                existing.get().setChild(request.getChild());
                existing.get().setPlanStatus(PlanStatus.ACTIVE);

                if(existing.get().isNotSameAuth(response.getAuthorization().getSignature())) {
                    SubscriptionAuth auth = createAuth(request.getEmailAddress(), response);
                    auth.setSubscription(existing.get());
                    existing.get().setAuth(auth);

                    subscriptionAuthRepository.findBySubscription_Id(existing.get().getId())
                            .ifPresent(subscriptionAuthRepository::delete);
                    subscriptionAuthRepository.save(auth);
                }
                existing.get().setUpdatedAt(LocalDateTime.now());
                existing.ifPresent(subscriptionRepository::save);
            } else {
                Subscription subscription = new Subscription();
                subscription.setPlan(request.getParent());
                subscription.setChild(request.getChild());
                subscription.setPlanStatus(PlanStatus.ACTIVE);
                subscription.setUpdatedAt(LocalDateTime.now());
                subscription.setUser(user.get());

                SubscriptionAuth auth = createAuth(request.getEmailAddress(), response);
                auth.setSubscription(subscription);
                subscription.setAuth(auth);
                subscriptionRepository.save(subscription);
            }
        } else {
            return ;
        }
    }

    private static SubscriptionAuth createAuth(String emailAddress, PaymentVerificationData response) {
        SubscriptionAuth auth = new SubscriptionAuth();
        auth.setBank(response.getAuthorization().getBank());
        auth.setCode(response.getAuthorization().getAuthorizationCode());
        auth.setBin(response.getAuthorization().getBin());
        auth.setChannel(response.getAuthorization().getChannel());
        auth.setAccountName(response.getAuthorization().getAccountName());
        auth.setCardType(response.getAuthorization().getCardType());
        auth.setCountryCode(response.getAuthorization().getCountryCode());
        auth.setExpMonth(response.getAuthorization().getExpMonth());
        auth.setExpYear(response.getAuthorization().getExpYear());
        auth.setLast4(response.getAuthorization().getLast4());
        auth.setReusable(response.getAuthorization().getReusable());
        auth.setSignature(response.getAuthorization().getSignature());
        auth.setEmailAddress(emailAddress);
        return auth;
    }
}
