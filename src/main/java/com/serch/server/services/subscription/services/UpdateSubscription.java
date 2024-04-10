package com.serch.server.services.subscription.services;

import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.models.subscription.SubscriptionAuth;
import com.serch.server.repositories.subscription.SubscriptionAuthRepository;
import com.serch.server.repositories.subscription.SubscriptionRepository;
import com.serch.server.services.payment.core.PaymentService;
import com.serch.server.services.payment.requests.PaymentChargeRequest;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateSubscription implements UpdateSubscriptionService {
    private final PaymentService paymentService;
    private final InitSubscriptionService initService;
    private final VerifySubscriptionService verifyService;
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionAuthRepository subscriptionAuthRepository;

    @Override
    public void checkSubscriptions() {
        subscriptionRepository.findAll()
                .stream()
                .filter(subscription ->
                        TimeUtil.isExpired(subscription.getSubscribedAt()) && subscription.getRetries() < 3
                )
                .toList()
                .forEach(subscription -> {
                    subscription.setPlanStatus(PlanStatus.EXPIRED);
                    subscription.setUpdatedAt(LocalDateTime.now());
                    subscriptionRepository.save(subscription);

                    if(subscription.getAuth() != null && subscription.getPlan().getType() != PlanType.FREE) {
                        trySubscription(subscription);
                    } else {
                        /// Send an email notification to the account owner about charge failure
                        /// TODO:: Create Subscription expired email template
                    }
                });
    }

    private void trySubscription(Subscription subscription) {
        try {
            PaymentVerificationData data = chargeAndVerify(subscription);

            subscription.setPlanStatus(PlanStatus.ACTIVE);
            if(subscription.isNotSameAuth(data.getAuthorization().getSignature())) {
                subscriptionAuthRepository.delete(subscription.getAuth());

                SubscriptionAuth auth = verifyService.createAuth(subscription.getUser().getEmailAddress(), data);
                auth.setSubscription(subscription);
                subscriptionAuthRepository.save(auth);
            }
            subscription.setUpdatedAt(LocalDateTime.now());
            subscription.setRetries(0);
            subscription.setSubscribedAt(LocalDateTime.now());
            subscriptionRepository.save(subscription);

            verifyService.createInvoice(subscription, String.valueOf(data.getAmount()));
        } catch (Exception e) {
            subscription.setRetries(subscription.getRetries() + 1);
            if(subscription.getRetries() == 3) {
                /// Send an email notification to the account owner about charge failure
                /// TODO:: Create timed out subscription tries email template
            }
        }
    }

    private PaymentVerificationData chargeAndVerify(Subscription subscription) {
        PaymentChargeRequest request = new PaymentChargeRequest();

        if(subscription.getUser().isProfile()) {
            request.setAmount(
                    String.valueOf(
                            Integer.parseInt(subscriptionService.getAmountFromUserActivePlan(subscription))
                    )
            );
        } else {
            request.setAmount(
                    String.valueOf(
                            Integer.parseInt(
                                    subscriptionService.getAmountFromUserActivePlan(subscription)
                            ) * initService.getBusinessSize(subscription)
                    )
            );
        }

        request.setEmail(subscription.getUser().getEmailAddress());
        request.setAuthorizationCode(subscription.getAuth().getCode());
        return paymentService.charge(request);
    }
}
