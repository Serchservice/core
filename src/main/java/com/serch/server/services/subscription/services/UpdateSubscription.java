package com.serch.server.services.subscription.services;

import com.serch.server.enums.email.EmailType;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.models.account.Profile;
import com.serch.server.models.business.BusinessProfile;
import com.serch.server.models.business.BusinessSubscription;
import com.serch.server.models.email.SendEmail;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.repositories.business.BusinessProfileRepository;
import com.serch.server.repositories.subscription.SubscriptionRepository;
import com.serch.server.services.email.services.EmailTemplateService;
import com.serch.server.services.payment.core.PaymentService;
import com.serch.server.services.payment.requests.PaymentChargeRequest;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import com.serch.server.services.transaction.services.InvoiceService;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is the class that implements and works on the logic for its main wrapper class.
 * It works to update and check on subscriptions to update payments where necessary.
 * <p></p>
 * @see  UpdateSubscriptionService
 */
@Service
@RequiredArgsConstructor
public class UpdateSubscription implements UpdateSubscriptionService {
    private final EmailTemplateService emailTemplateService;
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final BusinessProfileRepository businessProfileRepository;

    @Override
    public void checkSubscriptions() {
        subscriptionRepository.findAll()
                .stream()
                .filter(subscription -> TimeUtil.isExpired(subscription.getSubscribedAt()) && subscription.getRetries() < 3)
                .toList()
                .forEach(subscription -> {
                    subscription.setPlanStatus(PlanStatus.EXPIRED);
                    subscription.setUpdatedAt(LocalDateTime.now());
                    subscriptionRepository.save(subscription);

                    if(subscription.getAuth() != null) {
                        trySubscription(subscription);
                    } else {
                        sendEmail(subscription);
                    }
                });
    }

    /**
     * Attempts to charge and verify the payment for a subscription.
     * If successful, updates the subscription status and creates an invoice.
     * If unsuccessful, increments the retry count and handles retries.
     * @param subscription The subscription to be charged and verified.
     */
    private void trySubscription(Subscription subscription) {
        try {
            if(subscription.getUser().isBusiness()) {
                Optional<BusinessProfile> business = businessProfileRepository.findById(subscription.getUser().getId());
                if(business.isPresent()) {
                    List<Profile> profiles = new ArrayList<>();
                    if(business.get().getSubscriptions().isEmpty()) {
                        if(!business.get().getAssociates().isEmpty()) {
                            profiles = business.get().getAssociates();
                        }
                    } else {
                        profiles = business.get().getSubscriptions()
                                .stream()
                                .filter(s -> !s.isSuspended())
                                .map(BusinessSubscription::getProfile).toList();
                    }

                    PaymentChargeRequest request = new PaymentChargeRequest();
                    if(!profiles.isEmpty()) {
                        request.setAmount(String.valueOf(Integer.parseInt(subscriptionService.getActiveAmount(subscription)) * profiles.size()));
                        charge(subscription, request);
                        invoiceService.createInvoice(subscription, profiles);
                    } else {
                        sendFailedEmailNotification(subscription);
                    }
                }
            } else {
                PaymentChargeRequest request = new PaymentChargeRequest();
                request.setAmount(String.valueOf(Integer.parseInt(subscriptionService.getActiveAmount(subscription))));
                charge(subscription, request);
                invoiceService.createInvoice(subscription, List.of());
            }
        } catch (Exception e) {
            sendFailedEmailNotification(subscription);
        }
    }

    private void charge(Subscription subscription, PaymentChargeRequest request) {
        request.setEmail(subscription.getUser().getEmailAddress());
        request.setAuthorizationCode(subscription.getAuth().getCode());
        PaymentVerificationData data = paymentService.charge(request);
        subscription.setPlanStatus(PlanStatus.ACTIVE);
        if(subscription.isNotSameAuth(data.getAuthorization().getSignature())) {
            subscriptionService.createAuth(subscription, data);
        }
        subscription.setUpdatedAt(LocalDateTime.now());
        subscription.setRetries(0);
        subscription.setReference(data.getReference());
        subscription.setAmount(BigDecimal.valueOf(data.getAmount()));
        subscription.setSubscribedAt(LocalDateTime.now());
        subscriptionRepository.save(subscription);
    }

    private void sendFailedEmailNotification(Subscription subscription) {
        subscription.setRetries(subscription.getRetries() + 1);
        if(subscription.getRetries() == 3) {
            sendEmail(subscription);
        }
    }

    private void sendEmail(Subscription subscription) {
        SendEmail email = new SendEmail();
        email.setTo(subscription.getUser().getEmailAddress());
        email.setFirstName(subscription.getUser().getFirstName());
        email.setType(EmailType.UNSUCCESSFUL_PAYMENT);
        emailTemplateService.send(email);
    }
}