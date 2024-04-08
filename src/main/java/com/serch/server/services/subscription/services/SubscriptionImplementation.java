package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.exceptions.subscription.SubscriptionException;
import com.serch.server.mappers.SubscriptionMapper;
import com.serch.server.models.auth.User;
import com.serch.server.models.subscription.PlanBenefit;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.subscription.SubscriptionRepository;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.subscription.requests.InitSubscriptionRequest;
import com.serch.server.services.subscription.requests.VerifySubscriptionRequest;
import com.serch.server.services.subscription.responses.PlanParentResponse;
import com.serch.server.services.subscription.responses.SubscriptionCardResponse;
import com.serch.server.services.subscription.responses.SubscriptionResponse;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionImplementation implements SubscriptionService {
    private final InitSubscriptionService initService;
    private final PlanService planService;
    private final VerifySubscriptionService verifyService;
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

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
    public ApiResponse<SubscriptionResponse> seeCurrentSubscription() {
        Subscription subscription = subscriptionRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new SubscriptionException("Subscription not found"));

        SubscriptionResponse response = SubscriptionMapper.INSTANCE.subscription(subscription.getPlan());
        response.setAmount(getAmountFromUserActivePlan(subscription));
        response.setBenefits(subscription.getPlan().getBenefits().stream().map(PlanBenefit::getBenefit).toList());
        response.setDuration(getDuration(subscription));
        response.setPlan(subscription.getPlan().getType().getType());
        response.setRemaining(
                TimeUtil.formatPlanTime(
                        subscription.getSubscribedAt(),
                        subscription.getPlan().getType(),
                        subscription.getChild().getType()
                )
        );
        response.setChild(getChild(subscription));
        response.setStatus(subscription.getPlanStatus().getType());

        SubscriptionCardResponse card = SubscriptionMapper.INSTANCE.response(subscription.getAuth());
        card.setCard(subscription.getAuth().getBin() + "********" + subscription.getAuth().getLast4());
        card.setExpDate(subscription.getAuth().getExpYear() + "/" + subscription.getAuth().getExpMonth());

        response.setCard(card);
        return new ApiResponse<>(response);
    }

    @Override
    public String getAmountFromUserActivePlan(Subscription subscription) {
        if (subscription.getChild() != null) {
            return subscription.getChild().getAmount();
        }
        return subscription.getPlan().getAmount();
    }

    private static String getDuration(Subscription subscription) {
        return subscription.getChild() != null
                ? subscription.getChild().getName()
                : subscription.getPlan().getDuration();
    }

    private static String getChild(Subscription subscription) {
        return subscription.getChild() != null
                ? subscription.getChild().getType().getType()
                : subscription.getPlan().getDuration();
    }

    @Override
    public ApiResponse<List<PlanParentResponse>> getPlans() {
        Subscription subscription = subscriptionRepository.findByUser_Id(userUtil.getUser().getId())
                .orElse(new Subscription());

        ApiResponse<List<PlanParentResponse>> plans = planService.getPlans();
        if(plans.getStatus().is2xxSuccessful()) {
            return new ApiResponse<>(
                    "Successfully fetch plans",
                    plans.getData()
                            .stream()
                            .filter(res -> res.getType() != PlanType.FREE && !subscription.canUseFreePlan())
                            .toList(),
                    HttpStatus.OK
            );
        } else {
            throw new SubscriptionException(plans.getMessage());
        }
    }

    @Override
    public ApiResponse<String> unsubscribe() {
        Subscription subscription = subscriptionRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new SubscriptionException("Subscription not found"));
        subscription.setPlanStatus(PlanStatus.SUSPENDED);
        subscription.setUpdatedAt(LocalDateTime.now());
        subscriptionRepository.save(subscription);
        return new ApiResponse<>("Subscription cancelled", HttpStatus.OK);
    }
}
