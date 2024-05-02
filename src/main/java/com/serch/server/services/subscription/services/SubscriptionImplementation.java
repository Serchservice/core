package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.subscription.PlanException;
import com.serch.server.exceptions.subscription.SubscriptionException;
import com.serch.server.mappers.SubscriptionMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.business.BusinessProfile;
import com.serch.server.models.business.BusinessSubscription;
import com.serch.server.models.subscription.*;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.business.BusinessProfileRepository;
import com.serch.server.repositories.subscription.*;
import com.serch.server.services.payment.core.PaymentService;
import com.serch.server.services.payment.requests.InitializePaymentRequest;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import com.serch.server.services.subscription.requests.InitializeSubscriptionRequest;
import com.serch.server.services.subscription.responses.SubscriptionCardResponse;
import com.serch.server.services.subscription.responses.SubscriptionResponse;
import com.serch.server.services.transaction.services.InvoiceService;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This is the class that holds the logic and implementation for its wrapper class.
 *
 * @see SubscriptionService
 * @see PaymentService
 * @see InvoiceService
 */
@Service
@RequiredArgsConstructor
public class SubscriptionImplementation implements SubscriptionService {
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final UserUtil userUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final ProfileRepository profileRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;
    private final SubscriptionRequestAssociateRepository subscriptionRequestAssociateRepository;
    private final PlanParentRepository planParentRepository;
    private final PlanChildRepository planChildRepository;
    private final SubscriptionAuthRepository subscriptionAuthRepository;
    private final BusinessProfileRepository businessProfileRepository;

    @Override
    public ApiResponse<InitializePaymentData> subscribe(InitializeSubscriptionRequest request) {
        User user = userUtil.getUser();
        if(user.hasSubscription() && user.getSubscription().isActive()) {
            throw new SubscriptionException("You have an active subscription");
        }

        if(user.isBusiness()) {
            BusinessProfile business = businessProfileRepository.findById(user.getId())
                    .orElseThrow(() -> new SubscriptionException("Business not found"));
            List<Profile> associates;
            if(request.getAssociates().isEmpty()) {
                // Check onboarded associate providers
                if(business.getSubscriptions().isEmpty()) {
                    if(business.getAssociates().isEmpty()) {
                        throw new SubscriptionException("Cannot subscribe until business onboards associate providers");
                    } else {
                        associates = business.getAssociates();
                    }
                } else {
                    associates = business.getSubscriptions()
                            .stream()
                            .filter(s -> !s.isSuspended())
                            .map(BusinessSubscription::getProfile).toList();
                }
            } else {
                associates = profileRepository.findAllById(request.getAssociates());
            }
            if(associates.isEmpty()) {
                throw new SubscriptionException("Couldn't find associate providers for your business");
            }
            return subscribe(associates, user, request);
        } else {
            return subscribe(List.of(), user, request);
        }
    }

    private ApiResponse<InitializePaymentData> subscribe(List<Profile> profiles, User user, InitializeSubscriptionRequest request) {
        Optional<PlanParent> parent = planParentRepository.findById(request.getPlan());
        if(parent.isPresent()) {
            if(parent.get().getType() == PlanType.FREE) {
                if(user.hasSubscription() && user.getSubscription().canUseFreePlan()) {
                    return freeSubscription(user, profiles, parent.get());
                } else {
                    throw new SubscriptionException(
                            "You cannot use Serch free plan. Please activate your account with a paid plan."
                    );
                }
            } else {
                return paidSubscription(user, profiles, parent.get(), null, request.getCallbackUrl());
            }
        } else {
            Optional<PlanChild> planChild = planChildRepository.findById(request.getPlan());
            if(planChild.isPresent()) {
                return paidSubscription(user, profiles, planChild.get().getParent(), planChild.get(), request.getCallbackUrl());
            } else {
                throw new PlanException("Plan not found");
            }
        }
    }

    /**
     * Subscribe to free plan
     *
     * @param user User making the subscription
     * @param profiles Associated providers tied to the subscription
     * @param parent The Plan Parent
     * @return ApiResponse of {@link InitializePaymentData}
     *
     * @see ApiResponse
     */
    private ApiResponse<InitializePaymentData> freeSubscription(User user, List<Profile> profiles, PlanParent parent) {
        String reference = "FREE-%s".formatted(UUID.randomUUID().toString().replaceAll("-", ""));
        request(user, profiles, parent, null, reference);

        InitializePaymentData data = new InitializePaymentData();
        data.setReference(reference);
        return new ApiResponse<>(data);
    }

    /**
     * Subscribe to free plan
     *
     * @param user User making the subscription
     * @param profiles Associated providers tied to the subscription
     * @param parent The Plan Parent
     * @param planChild The Plan Child
     * @param url The callback url to show after verification
     * @return ApiResponse of {@link InitializePaymentData}
     *
     * @see ApiResponse
     */
    private ApiResponse<InitializePaymentData> paidSubscription(User user, List<Profile> profiles, PlanParent parent, PlanChild planChild, String url) {
        InitializePaymentRequest payRequest = new InitializePaymentRequest();

        String amount;
        if(planChild != null) {
            amount = planChild.getAmount();
        } else {
            amount = parent.getAmount();
        }
        int size = profiles.isEmpty() ? 1 : profiles.size();
        payRequest.setAmount(String.valueOf(Integer.parseInt(amount) * size));

        payRequest.setEmail(user.getEmailAddress());
        payRequest.setCallbackUrl(url);
        payRequest.setChannels(new ArrayList<>(Collections.singletonList("card")));
        InitializePaymentData data = paymentService.initialize(payRequest);

        request(user, profiles, parent, planChild, data.getReference());
        return new ApiResponse<>(data);
    }

    /**
     * Add associates to a subscription request to the associate request table
     *
     * @param user User making the subscription
     * @param associates Associated providers tied to the subscription
     * @param parent The Plan Parent
     * @param child The Plan Child
     * @param reference The reference code for subscription verification
     */
    private void request(User user, List<Profile> associates, PlanParent parent, PlanChild child, String reference) {
        SubscriptionRequest request = getRequest(user, associates, parent, child, reference);

        if(!associates.isEmpty()) {
            associates.forEach(profile -> {
                SubscriptionRequestAssociate associate = new SubscriptionRequestAssociate();
                associate.setRequest(request);
                associate.setProfile(profile);
                subscriptionRequestAssociateRepository.save(associate);
            });
        }
    }

    /**
     * Add the subscription request to the request table
     *
     * @param user User making the subscription
     * @param associates Associated providers tied to the subscription
     * @param parent The Plan Parent
     * @param child The Plan Child
     * @param reference The reference code for subscription verification
     * @return Subscription request {@link SubscriptionRequest}
     */
    private SubscriptionRequest getRequest(User user, List<Profile> associates, PlanParent parent, PlanChild child, String reference) {
        Optional<SubscriptionRequest> subRequest = subscriptionRequestRepository.findByUser_Id(user.getId());
        subRequest.ifPresent(subscriptionRequestRepository::delete);

        SubscriptionRequest subscription = new SubscriptionRequest();
        subscription.setUser(user);
        subscription.setParent(parent);
        subscription.setSize(associates.isEmpty() ? 1 : associates.size());
        subscription.setReference(reference);

        if(child != null) {
            subscription.setChild(child);
        }
        return subscriptionRequestRepository.save(subscription);
    }

    @Override
    public ApiResponse<String> verify(String reference) {
        User user = userUtil.getUser();
        SubscriptionRequest request = subscriptionRequestRepository.findByReferenceAndUser_Id(reference, user.getId())
                .orElseThrow(() -> new SubscriptionException("Subscription request not found"));

        if(request.getParent().getType() == PlanType.FREE) {
            return verifyFree(request, user);
        } else {
            return verifyPaid(request, user);
        }
    }

    /**
     * Verifies a free subscription request.
     * If the user is already registered,
     * updates the subscription details; otherwise, creates a new subscription.
     * @param request The subscription request to be verified.
     * @param user The user associated with the subscription request.
     * @return An ApiResponse indicating the status of the verification process.
     */
    private ApiResponse<String> verifyFree(SubscriptionRequest request, User user) {
        Optional<Subscription> existing = subscriptionRepository.findByUser_Id(user.getId());
        if(existing.isPresent()) {
            if(existing.get().canUseFreePlan()) {
                existing.get().setFreePlanStatus(PlanStatus.USED);
                Subscription updatedSubscription = updateSubscription(request, existing.get());
                invoiceService.createInvoice(
                        updatedSubscription,
                        !request.getAssociates().isEmpty()
                                ? request.getAssociates().stream().map(SubscriptionRequestAssociate::getProfile).toList()
                                : List.of()
                );
                subscriptionRequestRepository.delete(request);
                return new ApiResponse<>("Success", HttpStatus.OK);
            } else {
                throw new SubscriptionException("You cannot subscribe to free plan anymore");
            }
        } else {
            createSubscription(request, user, null);
            return new ApiResponse<>("Success", HttpStatus.OK);
        }
    }

    /**
     * Verifies a paid subscription request. If the user is already registered,
     * updates the subscription details; otherwise, creates a new subscription.
     * @param request The subscription request to be verified.
     * @param user The user associated with the subscription request.
     * @return An ApiResponse indicating the status of the verification process.
     */
    private ApiResponse<String> verifyPaid(SubscriptionRequest request, User user) {
        PaymentVerificationData data = paymentService.verify(request.getReference());

        Optional<Subscription> existing = subscriptionRepository.findByUser_Id(user.getId());
        if(existing.isPresent()) {
            Subscription updatedSubscription = updateSubscription(request, existing.get());
            if(updatedSubscription.isNotSameAuth(data.getAuthorization().getSignature())) {
                createAuth(updatedSubscription, data);
            }
            invoiceService.createInvoice(
                    updatedSubscription,
                    !request.getAssociates().isEmpty()
                            ? request.getAssociates().stream().map(SubscriptionRequestAssociate::getProfile).toList()
                            : List.of()
            );
            subscriptionRequestRepository.delete(request);
        } else {
            createSubscription(request, user, data);
        }
        return new ApiResponse<>("Success", HttpStatus.OK);
    }

    /// Update existing subscription
    public Subscription updateSubscription(SubscriptionRequest request, Subscription existing) {
        existing.setPlan(request.getParent());
        existing.setChild(request.getChild());
        existing.setPlanStatus(PlanStatus.ACTIVE);
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setSubscribedAt(LocalDateTime.now());
        existing.setReference(request.getReference());
        existing.setMode(request.getMode());
        existing.setSize(request.getSize());
        existing.setRetries(0);
        return subscriptionRepository.save(existing);
    }

    /// Create authorization record for subscription
    @Override
    public void createAuth(Subscription subscription, PaymentVerificationData data) {
        subscriptionAuthRepository.delete(subscription.getAuth());
        SubscriptionAuth auth = SubscriptionMapper.INSTANCE.auth(data.getAuthorization());
        auth.setEmailAddress(subscription.getUser().getEmailAddress());
        auth.setSubscription(subscription);
        subscriptionAuthRepository.save(auth);
    }

    /**
     * Creates a new subscription for a given user based on a subscription request.
     * @param request The subscription request.
     * @param user The user for whom the subscription is created.
     * @param data The payment verification data.
     */
    private void createSubscription(SubscriptionRequest request, User user, PaymentVerificationData data) {
        Subscription subscribed = getSubscription(request, user, data);
        if(data != null) {
            createAuth(subscribed, data);
        }
        invoiceService.createInvoice(
                subscribed,
                !request.getAssociates().isEmpty()
                        ? request.getAssociates().stream().map(SubscriptionRequestAssociate::getProfile).toList()
                        : List.of()
        );
        subscriptionRequestRepository.delete(request);
    }

    /// Prepare subscription data and return it
    private Subscription getSubscription(SubscriptionRequest request, User user, PaymentVerificationData data) {
        Subscription subscription = SubscriptionMapper.INSTANCE.subscription(request);
        subscription.setPlanStatus(PlanStatus.ACTIVE);
        subscription.setUpdatedAt(LocalDateTime.now());
        subscription.setUser(user);
        subscription.setSubscribedAt(LocalDateTime.now());
        if(request.getParent().getType() == PlanType.FREE) {
            subscription.setFreePlanStatus(PlanStatus.USED);
        }
        if(data != null) {
            subscription.setAmount(BigDecimal.valueOf(data.getAmount()));
        }
        return subscriptionRepository.save(subscription);
    }

    @Override
    public ApiResponse<SubscriptionResponse> seeCurrentSubscription() {
        User user = userUtil.getUser();
        Optional<Subscription> subscription;
        if(user.getRole() == Role.ASSOCIATE_PROVIDER) {
            Profile profile = profileRepository.findById(user.getId())
                    .orElseThrow(() -> new SubscriptionException("Provider not found"));
            subscription = subscriptionRepository.findByUser_Id(profile.getBusiness().getId());

            if(profile.getBusiness().getSubscriptions().isEmpty()) {
                throw new SubscriptionException(
                        "Your business organization has no active plan. Contact the admin to activate your account.",
                        ExceptionCodes.NO_SUBSCRIPTION
                );
            } else if(profile.getBusiness().getSubscriptions().stream().noneMatch(sub -> sub.getProfile().isSameAs(profile.getId()))) {
                throw new SubscriptionException(
                        "Your business admin has not added you to the list of business subscriptions. Contact your admin.",
                        ExceptionCodes.NO_SUBSCRIPTION
                );
            } else if(profile.getBusiness().getSubscriptions().stream().anyMatch(sub -> sub.getProfile().isSameAs(profile.getId()) && sub.isSuspended())) {
                throw new SubscriptionException(
                        "Your business admin has removed your account from the list of organization subscriptions. Contact the admin if this is an error.",
                        ExceptionCodes.NO_SUBSCRIPTION
                );
            } else if(profile.getBusiness().getSubscriptions().stream().anyMatch(sub -> sub.getProfile().isSameAs(profile.getId()) && sub.isNotActive())) {
                throw new SubscriptionException(
                        "Your organization subscription has ended or not active. Contact your business admin.",
                        ExceptionCodes.NO_SUBSCRIPTION
                );
            }
        } else {
            subscription = subscriptionRepository.findByUser_Id(userUtil.getUser().getId());
        }

        if(subscription.isPresent()) {
            return new ApiResponse<>(getSubscriptionResponse(subscription.get()));
        } else {
            throw new SubscriptionException(
                    "You have no subscription. Activate your account by choosing a plan",
                    ExceptionCodes.NO_SUBSCRIPTION
            );
        }
    }

    private SubscriptionResponse getSubscriptionResponse(Subscription subscription) {
        SubscriptionResponse response = SubscriptionMapper.INSTANCE.subscription(subscription.getPlan());
        response.setAmount(getActiveAmount(subscription));
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
        response.setIsActive(subscription.isActive());
        response.setId(getId(subscription));
        response.setChild(getChild(subscription));
        response.setStatus(subscription.getPlanStatus().getType());

        SubscriptionCardResponse card = SubscriptionMapper.INSTANCE.response(subscription.getAuth());
        card.setCard(subscription.getAuth().getBin() + "********" + subscription.getAuth().getLast4());
        card.setExpDate(subscription.getAuth().getExpYear() + "/" + subscription.getAuth().getExpMonth());

        response.setCard(card);
        return response;
    }

    /**
     * Retrieves the duration of the subscription.
     * @param subscription The subscription for which to retrieve the duration.
     * @return The duration of the subscription.
     */
    private static String getDuration(Subscription subscription) {
        return subscription.getChild() != null
                ? subscription.getChild().getName()
                : subscription.getPlan().getDuration();
    }

    /**
     * Retrieves the child type of the subscription.
     * @param subscription The subscription for which to retrieve the child type.
     * @return The child type of the subscription.
     */
    private static String getChild(Subscription subscription) {
        return subscription.getChild() != null
                ? subscription.getChild().getType().getType()
                : subscription.getPlan().getDuration();
    }

    /**
     * Retrieves the id of the current plan
     * @param subscription The subscription for which to retrieve the plan id.
     * @return The plan id of the subscription.
     */
    private static String getId(Subscription subscription) {
        return subscription.getChild() != null
                ? subscription.getChild().getId()
                : subscription.getPlan().getId();
    }

    @Override
    public String getActiveAmount(Subscription subscription) {
        if (subscription.getChild() != null) {
            return subscription.getChild().getAmount();
        }
        return subscription.getPlan().getAmount();
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