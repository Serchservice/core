package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.exceptions.subscription.PlanException;
import com.serch.server.exceptions.subscription.SubscriptionException;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.models.subscription.PlanChild;
import com.serch.server.models.subscription.PlanParent;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.models.subscription.SubscriptionRequest;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import com.serch.server.repositories.subscription.PlanChildRepository;
import com.serch.server.repositories.subscription.PlanParentRepository;
import com.serch.server.repositories.subscription.SubscriptionRepository;
import com.serch.server.repositories.subscription.SubscriptionRequestRepository;
import com.serch.server.services.payment.core.PaymentService;
import com.serch.server.services.payment.requests.InitializePaymentRequest;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.subscription.requests.InitSubscriptionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InitSubscription implements InitSubscriptionService {
    private final PaymentService paymentService;
    private final SubscriptionRepository subscriptionRepository;
    private final IncompleteRepository incompleteRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;
    private final PlanParentRepository planParentRepository;
    private final PlanChildRepository planChildRepository;
    private final BusinessProfileRepository businessProfileRepository;

    @Override
    public ApiResponse<InitializePaymentData> subscribe(User user, InitSubscriptionRequest request) {
        Optional<Subscription> subscription = subscriptionRepository.findByUser_Id(user.getId());

        request.setEmailAddress(user.getEmailAddress());
        if(subscription.isPresent()) {
            if(subscription.get().isActive()) {
                throw new SubscriptionException("You have an active subscription");
            } else {
                return subscribe(request, subscription);
            }
        } else {
            return subscribe(request, Optional.empty());
        }
    }

    @Override
    public ApiResponse<InitializePaymentData> subscribe(InitSubscriptionRequest request) {
        Incomplete incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new AuthException("User not found"));

        if(incomplete.isEmailConfirmed()) {
            if(incomplete.hasProfile()) {
                if(incomplete.hasCategory()) {
                    if(incomplete.hasAdditional()) {
                        request.setEmailAddress(incomplete.getEmailAddress());
                        return subscribe(request, Optional.empty());
                    } else {
                        throw new AuthException(
                                "You don't have a additional profile", ExceptionCodes.CATEGORY_NOT_SET
                        );
                    }
                } else {
                    throw new AuthException(
                            "You don't have a Serch category", ExceptionCodes.CATEGORY_NOT_SET
                    );
                }
            } else {
                throw new AuthException("You don't have any profile", ExceptionCodes.PROFILE_NOT_SET);
            }
        } else {
            throw new AuthException("You have not confirmed your email", ExceptionCodes.EMAIL_NOT_VERIFIED);
        }
    }

    private ApiResponse<InitializePaymentData> subscribe(InitSubscriptionRequest request, Optional<Subscription> user) {
        PlanParent parent = planParentRepository.findById(request.getPlan())
                .orElseThrow(() -> new PlanException("Plan not found"));

        if(parent.getType() == PlanType.FREE) {
            if(user.isPresent()) {
                if(user.get().canUseFreePlan()) {
                    if(user.get().getUser().isProfile()) {
                        return subscribeToFree(request, parent, 1);
                    } else {
                        return subscribeToFree(request, parent, getBusinessSize(user.get()));
                    }
                } else {
                    throw new SubscriptionException(
                            "You cannot use Serch free plan. Please activate your account with a paid plan."
                    );
                }
            } else {
                return subscribeToFree(request, parent, 1);
            }
        } else {
            PlanChild planChild;
            if(request.getChild() != null && !request.getChild().isEmpty()) {
                planChild = planChildRepository.findById(request.getChild())
                        .orElseThrow(() -> new PlanException("Plan not found"));
            } else {
                planChild = null;
            }

            if(user.isPresent()) {
                if(user.get().getUser().isProfile()) {
                    return subscribeToPaid(
                            request, user.get().getUser().getEmailAddress(),
                            1, planChild, parent
                    );
                } else {
                    return subscribeToPaid(
                            request, user.get().getUser().getEmailAddress(),
                            getBusinessSize(user.get()), planChild, parent
                    );
                }
            } else {
                return subscribeToPaid(
                        request, request.getEmailAddress(), request.getSize(),
                        planChild, parent
                );
            }
        }
    }

    @Override
    public int getBusinessSize(Subscription user) {
        BusinessProfile profile = businessProfileRepository.findById(user.getUser().getId())
                .orElseThrow(() -> new SubscriptionException("Business not found"));

        int size = profile.getAssociates().stream()
                .filter(sub -> !sub.getUser().isBusinessLocked())
                .toList()
                .size();

        if(size == 0) {
            throw new SubscriptionException("Business has no providers");
        }
        return size;
    }

    private ApiResponse<InitializePaymentData> subscribeToPaid(
            InitSubscriptionRequest request, String emailAddress, int size,
            PlanChild planChild, PlanParent parent
    ) {
        InitializePaymentRequest payRequest = new InitializePaymentRequest();

        String amount;
        if(planChild != null) {
            amount = planChild.getAmount();
        } else {
            amount = parent.getAmount();
        }
        payRequest.setAmount(String.valueOf(Integer.parseInt(amount) * size));

        payRequest.setEmail(emailAddress);
        payRequest.setCallbackUrl(request.getCallbackUrl());
        payRequest.setChannels(new ArrayList<>(Collections.singletonList("card")));
        InitializePaymentData data = paymentService.initialize(payRequest);

        addToRequest(parent, emailAddress, planChild, data.getReference(), size);
        return new ApiResponse<>(data);
    }

    private ApiResponse<InitializePaymentData> subscribeToFree(
            InitSubscriptionRequest request, PlanParent parent, int size
    ) {
        String reference = "FREE-%s".formatted(UUID.randomUUID().toString().replaceAll("-", ""));
        addToRequest(parent, request.getEmailAddress(), null, reference, size);

        InitializePaymentData data = new InitializePaymentData();
        data.setReference(reference);
        return new ApiResponse<>(data);
    }

    private void addToRequest(
            PlanParent parent, String emailAddress, PlanChild child,
            String reference, Integer size
    ) {
        Optional<SubscriptionRequest> subRequest = subscriptionRequestRepository.findByEmailAddress(emailAddress);
        subRequest.ifPresent(subscriptionRequestRepository::delete);

        SubscriptionRequest subscription = new SubscriptionRequest();
        subscription.setEmailAddress(emailAddress);
        subscription.setParent(parent);
        subscription.setSize(size);
        subscription.setReference(reference);

        if(child != null) {
            subscription.setChild(child);
        }
        subscriptionRequestRepository.save(subscription);
    }
}
