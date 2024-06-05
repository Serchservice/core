package com.serch.server.services.subscription.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
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
import com.serch.server.repositories.business.BusinessSubscriptionRepository;
import com.serch.server.repositories.subscription.*;
import com.serch.server.services.payment.core.PaymentService;
import com.serch.server.services.payment.requests.InitializePaymentRequest;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import com.serch.server.services.subscription.requests.InitializeSubscriptionRequest;
import com.serch.server.services.subscription.responses.CurrentSubscriptionResponse;
import com.serch.server.services.subscription.responses.SubscriptionCardResponse;
import com.serch.server.services.subscription.responses.SubscriptionInvoiceResponse;
import com.serch.server.services.subscription.services.SubscriptionService;
import com.serch.server.services.transaction.responses.AssociateTransactionData;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This is the class that holds the logic and implementation for its wrapper class.
 *
 * @see SubscriptionService
 * @see PaymentService
 */
@Service
@RequiredArgsConstructor
public class SubscriptionImplementation implements SubscriptionService {
    private final PaymentService paymentService;
    private final UserUtil userUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final ProfileRepository profileRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;
    private final SubscriptionRequestAssociateRepository subscriptionRequestAssociateRepository;
    private final PlanParentRepository planParentRepository;
    private final PlanChildRepository planChildRepository;
    private final SubscriptionAuthRepository subscriptionAuthRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final SubscriptionInvoiceRepository subscriptionInvoiceRepository;
    private final SubscriptionAssociateRepository subscriptionAssociateRepository;
    private final BusinessSubscriptionRepository businessSubscriptionRepository;

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
            if(business.getSubscriptions() == null || business.getSubscriptions().isEmpty()) {
                if(business.getAssociates() == null || business.getAssociates().isEmpty()) {
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

            if(associates.isEmpty()) {
                throw new SubscriptionException("Couldn't find associate providers for your business");
            }
            return subscribe(associates, user, request);
        } else {
            return subscribe(List.of(), user, request);
        }
    }

    private ApiResponse<InitializePaymentData> subscribe(
            List<Profile> profiles, User user, InitializeSubscriptionRequest request
    ) {
        Optional<PlanParent> parent = planParentRepository.findById(request.getPlan());
        if(parent.isPresent()) {
            if(parent.get().getType() == PlanType.FREE) {
                if(user.hasSubscription()) {
                    if(user.getSubscription().canUseFreePlan()) {
                        return freeSubscription(user, profiles, parent.get());
                    } else {
                        throw new SubscriptionException(
                                "You cannot use Serch free plan. Please activate your account with a paid plan."
                        );
                    }
                } else {
                    return freeSubscription(user, profiles, parent.get());
                }
            } else {
                return paidSubscription(user, profiles, parent.get(), null, request.getCallbackUrl());
            }
        } else {
            PlanChild planChild = planChildRepository.findById(request.getPlan())
                    .orElseThrow(() -> new PlanException("Plan not found"));
            return paidSubscription(user, profiles, planChild.getParent(), planChild, request.getCallbackUrl());
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
    private ApiResponse<InitializePaymentData> paidSubscription(
            User user, List<Profile> profiles, PlanParent parent, PlanChild planChild, String url
    ) {
        InitializePaymentRequest payRequest = new InitializePaymentRequest();

        int amount;
        if(planChild != null) {
            amount = BigDecimal.valueOf(Double.parseDouble(planChild.getAmount())).intValue();
        } else {
            amount = BigDecimal.valueOf(Double.parseDouble(parent.getAmount())).intValue();
        }
        int size = profiles.isEmpty() ? 1 : profiles.size();
        payRequest.setAmount(String.valueOf(amount * size));
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

        if(associates != null && !associates.isEmpty()) {
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
        Optional<SubscriptionRequest> request = subscriptionRequestRepository.findByUser_Id(user.getId());

        AtomicReference<SubscriptionRequest> subscription = new AtomicReference<>(new SubscriptionRequest());
        request.ifPresentOrElse(sub -> {
            sub.setParent(parent);
            sub.setSize(associates.isEmpty() ? 1 : associates.size());
            sub.setReference(reference);

            if(child != null) {
                sub.setChild(child);
            }
            subscription.set(subscriptionRequestRepository.save(sub));
        }, () -> {
            SubscriptionRequest sub = new SubscriptionRequest();
            sub.setUser(user);
            sub.setParent(parent);
            sub.setSize(associates.isEmpty() ? 1 : associates.size());
            sub.setReference(reference);

            if(child != null) {
                sub.setChild(child);
            }
            subscription.set(subscriptionRequestRepository.save(sub));
        });
        return subscription.get();
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
            existing.get().setFreeStatus(PlanStatus.USED);
            Subscription updatedSubscription = updateSubscription(request, existing.get(), null);
            createInvoice(
                    updatedSubscription,
                    request.getAssociates() != null && !request.getAssociates().isEmpty()
                            ? request.getAssociates().stream().map(SubscriptionRequestAssociate::getProfile).toList()
                            : List.of()
            );
            subscriptionRequestRepository.delete(request);
        } else {
            createSubscription(request, user, null);
        }
        return new ApiResponse<>("Success", HttpStatus.OK);
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
            Subscription updatedSubscription = updateSubscription(request, existing.get(), data);
            if(updatedSubscription.isNotSameAuth(data.getAuthorization().getSignature())) {
                createAuth(updatedSubscription, data);
            }
            createInvoice(
                    updatedSubscription,
                    request.getAssociates() != null && !request.getAssociates().isEmpty()
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
    public Subscription updateSubscription(SubscriptionRequest request, Subscription sub, PaymentVerificationData data) {
        sub.setPlan(request.getParent());
        sub.setChild(request.getChild());
        sub.setStatus(PlanStatus.ACTIVE);
        sub.setUpdatedAt(LocalDateTime.now());
        sub.setSubscribedAt(LocalDateTime.now());
        sub.setReference(request.getReference());
        sub.setMode(request.getMode());
        sub.setSize(request.getSize());
        sub.setRetries(0);

        if(data != null) {
            sub.setAmount(BigDecimal.valueOf(data.getAmount() / 100));
        } else {
            sub.setAmount(BigDecimal.ZERO);
        }
        return subscriptionRepository.save(sub);
    }

    /// Create authorization record for subscription
    @Override
    public void createAuth(Subscription subscription, PaymentVerificationData data) {
        if(subscription.getAuth() != null) {
            subscription.getAuth().setBank(data.getAuthorization().getBank());
            subscription.getAuth().setCode(data.getAuthorization().getAuthorizationCode());
            subscription.getAuth().setCardType(data.getAuthorization().getCardType());
            subscription.getAuth().setLast4(data.getAuthorization().getLast4());
            subscription.getAuth().setExpMonth(data.getAuthorization().getExpMonth());
            subscription.getAuth().setExpYear(data.getAuthorization().getExpYear());
            subscription.getAuth().setBin(data.getAuthorization().getBin());
            subscription.getAuth().setChannel(data.getAuthorization().getChannel());
            subscription.getAuth().setSignature(data.getAuthorization().getSignature());
            subscription.getAuth().setCountryCode(data.getAuthorization().getCountryCode());
            subscription.getAuth().setReusable(data.getAuthorization().getReusable());
            subscription.getAuth().setAccountName(data.getAuthorization().getAccountName());
            subscription.getAuth().setUpdatedAt(LocalDateTime.now());
            subscriptionAuthRepository.save(subscription.getAuth());
        } else {
            SubscriptionAuth auth = SubscriptionMapper.INSTANCE.auth(data.getAuthorization());
            auth.setEmailAddress(subscription.getUser().getEmailAddress());
            auth.setSubscription(subscription);
            subscriptionAuthRepository.save(auth);
        }
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
        createInvoice(
                subscribed,
                request.getAssociates() != null && !request.getAssociates().isEmpty()
                        ? request.getAssociates().stream().map(SubscriptionRequestAssociate::getProfile).toList()
                        : List.of()
        );
        subscriptionRequestRepository.delete(request);
    }

    /// Prepare subscription data and return it
    private Subscription getSubscription(SubscriptionRequest request, User user, PaymentVerificationData data) {
        Subscription subscription = SubscriptionMapper.INSTANCE.subscription(request);
        subscription.setStatus(PlanStatus.ACTIVE);
        subscription.setUpdatedAt(LocalDateTime.now());
        subscription.setUser(user);
        subscription.setSubscribedAt(LocalDateTime.now());
        subscription.setPlan(request.getParent());
        if(request.getParent().getType() == PlanType.FREE) {
            subscription.setFreeStatus(PlanStatus.USED);
        }
        if(request.getChild() != null) {
            subscription.setChild(request.getChild());
        }
        if(data != null) {
            subscription.setAmount(BigDecimal.valueOf(data.getAmount() / 100));
        } else {
            subscription.setAmount(BigDecimal.ZERO);
        }
        return subscriptionRepository.save(subscription);
    }

    @Override
    public void checkSubscription() {
        User user = userUtil.getUser();
        if(user.getRole() == Role.ASSOCIATE_PROVIDER) {
            Profile profile = profileRepository.findById(user.getId())
                    .orElseThrow(() -> new SubscriptionException("Provider not found"));
            SubscriptionInvoice invoice = subscriptionInvoiceRepository.findActiveByUser(profile.getBusiness().getId())
                    .orElseThrow(() -> new SubscriptionException(
                            "Your business organization has no active plan. Contact the admin to activate your account.",
                            ExceptionCodes.NO_SUBSCRIPTION
                    ));
            if(invoice.getAssociates().stream().noneMatch(sub -> sub.getProfile().isSameAs(profile.getId()))) {
                throw new SubscriptionException(
                        "Your business admin has not added you to the list of business subscriptions. Contact your admin.",
                        ExceptionCodes.NO_SUBSCRIPTION
                );
            } else if(profile.getBusiness().getSubscriptions().stream().anyMatch(sub -> sub.getProfile().isSameAs(profile.getId()) && sub.isSuspended())) {
                throw new SubscriptionException(
                        "Your business admin has removed your account from the organization subscriptions. Contact the admin if this is an error.",
                        ExceptionCodes.NO_SUBSCRIPTION
                );
            }
        } else if(user.getRole() == Role.PROVIDER) {
            subscriptionInvoiceRepository.findActiveByUser(userUtil.getUser().getId())
                    .orElseThrow(() -> new SubscriptionException(
                            "You have no subscription. Activate your account by choosing a plan",
                            ExceptionCodes.NO_SUBSCRIPTION
                    ));
        }
    }

    @Override
    public ApiResponse<CurrentSubscriptionResponse> seeCurrentSubscription() {
        User user = userUtil.getUser();
        if(user.getRole() == Role.ASSOCIATE_PROVIDER) {
            Profile profile = profileRepository.findById(user.getId())
                    .orElseThrow(() -> new SubscriptionException("Provider not found"));
            SubscriptionInvoice invoice = subscriptionInvoiceRepository.findActiveByUser(profile.getBusiness().getId())
                    .orElseThrow(() -> new SubscriptionException(
                            "Your business organization has no active plan. Contact the admin to activate your account.",
                            ExceptionCodes.NO_SUBSCRIPTION
                    ));
            if(invoice.getAssociates().stream().noneMatch(sub -> sub.getProfile().isSameAs(profile.getId()))) {
                throw new SubscriptionException(
                        "Your business admin has not added you to the list of business subscriptions. Contact your admin.",
                        ExceptionCodes.NO_SUBSCRIPTION
                );
            } else if(profile.getBusiness().getSubscriptions().stream().anyMatch(sub -> sub.getProfile().isSameAs(profile.getId()) && sub.isSuspended())) {
                throw new SubscriptionException(
                        "Your business admin has removed your account from the organization subscriptions. Contact the admin if this is an error.",
                        ExceptionCodes.NO_SUBSCRIPTION
                );
            } else {
                return new ApiResponse<>("Subscription is active", HttpStatus.OK);
            }
        } else {
            SubscriptionInvoice invoice = subscriptionInvoiceRepository.findActiveByUser(userUtil.getUser().getId())
                    .orElseThrow(() -> new SubscriptionException(
                            "You have no subscription. Activate your account by choosing a plan",
                            ExceptionCodes.NO_SUBSCRIPTION
                    ));
            return new ApiResponse<>(getSubscriptionResponse(invoice.getSubscription()));
        }
    }

    private CurrentSubscriptionResponse getSubscriptionResponse(Subscription subscription) {
        CurrentSubscriptionResponse response = SubscriptionMapper.INSTANCE.subscription(subscription.getPlan());
        response.setAmount(MoneyUtil.formatToNaira(subscription.getAmount()));
        response.setBenefits(subscription.getPlan().getBenefits().stream().map(PlanBenefit::getBenefit).toList());
        response.setDuration(getDuration(subscription));
        response.setPlan(subscription.getPlan().getType().getType());
        response.setRemaining(TimeUtil.formatPlanTime(
                subscription.getSubscribedAt(),
                subscription.getPlan().getType(),
                subscription.getChild().getType()
        ));
        response.setIsActive(subscription.isActive());
        response.setId(subscription.getId());
        response.setChild(getChild(subscription));
        response.setStatus(subscription.getStatus().getType());
        response.setSize(subscription.getSize());

        if(subscription.getAuth() != null) {
            SubscriptionCardResponse card = SubscriptionMapper.INSTANCE.response(subscription.getAuth());
            card.setCard(subscription.getAuth().getBin() + "********" + subscription.getAuth().getLast4());
            card.setExpDate(subscription.getAuth().getExpYear() + "/" + subscription.getAuth().getExpMonth());
            response.setCard(card);
        }
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
        subscription.setStatus(PlanStatus.SUSPENDED);
        subscription.setUpdatedAt(LocalDateTime.now());
        subscriptionRepository.save(subscription);
        return new ApiResponse<>("Subscription cancelled", HttpStatus.OK);
    }

    @Override
    public void createInvoice(Subscription subscription, List<Profile> associates) {
        SubscriptionInvoice savedInvoice = getSubscriptionInvoice(subscription);
        if(associates != null && !associates.isEmpty()) {
            businessProfileRepository.findById(subscription.getUser().getId()).ifPresent(business -> associates.forEach(profile -> {
                SubscriptionAssociate associate = new SubscriptionAssociate();
                associate.setInvoice(savedInvoice);
                associate.setBusiness(business);
                associate.setProfile(profile);
                subscriptionAssociateRepository.save(associate);

                businessSubscriptionRepository.findByProfile_Id(profile.getId())
                        .ifPresentOrElse(businessSubscription -> {
                            businessSubscription.setStatus(AccountStatus.ACTIVE);
                            businessSubscription.setUpdatedAt(LocalDateTime.now());
                            businessSubscriptionRepository.save(businessSubscription);
                        }, () -> {
                            BusinessSubscription businessSubscription = new BusinessSubscription();
                            businessSubscription.setBusiness(business);
                            businessSubscription.setProfile(profile);
                            businessSubscription.setStatus(AccountStatus.ACTIVE);
                            businessSubscriptionRepository.save(businessSubscription);
                        });
            }));
        }
    }

    private SubscriptionInvoice getSubscriptionInvoice(Subscription subscription) {
        if(subscription.getInvoices() != null && !subscription.getInvoices().isEmpty()) {
            subscription.getInvoices().stream().filter(invoice -> invoice.getStatus() == PlanStatus.ACTIVE)
                    .forEach(invoice -> {
                        invoice.setStatus(PlanStatus.EXPIRED);
                        invoice.setUpdatedAt(LocalDateTime.now());
                        subscriptionInvoiceRepository.save(invoice);
                    });
        }
        SubscriptionInvoice invoice = SubscriptionMapper.INSTANCE.invoice(subscription);
        invoice.setAmount(String.valueOf(subscription.getAmount()));
        invoice.setPlan(
                subscription.getChild() != null
                        ? subscription.getChild().getName()
                        : subscription.getPlan().getType().getType()
        );
        invoice.setSubscription(subscription);
        return subscriptionInvoiceRepository.save(invoice);
    }

    @Override
    public ApiResponse<List<SubscriptionInvoiceResponse>> invoices() {
        List<SubscriptionInvoice> invoices = subscriptionInvoiceRepository.findBySubscription_User_Id(userUtil.getUser().getId());
        if(invoices != null && !invoices.isEmpty()) {
            return new ApiResponse<>(
                    invoices.stream()
                            .sorted(Comparator.comparing(SubscriptionInvoice::getCreatedAt).reversed())
                            .map(this::response).toList()
            );
        }
        return new ApiResponse<>(List.of());
    }

    private SubscriptionInvoiceResponse response(SubscriptionInvoice invoice) {
        SubscriptionInvoiceResponse response = SubscriptionMapper.INSTANCE.response(invoice);
        response.setSubscription(invoice.getSubscription().getId());
        response.setSubscribedAt(TimeUtil.formatDay(invoice.getCreatedAt()));
        response.setExpiredAt(TimeUtil.formatPlanTime(
                invoice.getSubscription().getSubscribedAt(),
                invoice.getSubscription().getPlan().getType(),
                invoice.getSubscription().getChild().getType()
        ));
        response.setAmount(MoneyUtil.formatToNaira(BigDecimal.valueOf(Double.parseDouble(invoice.getAmount()))));
        response.setImage(invoice.getSubscription().getPlan().getImage());
        if(invoice.getAssociates() != null && !invoice.getAssociates().isEmpty()) {
            response.setAssociates(
                    invoice.getAssociates()
                            .stream()
                            .map(associate -> AssociateTransactionData.builder()
                                    .name(associate.getProfile().getFullName())
                                    .category(associate.getProfile().getCategory().getType())
                                    .rating(associate.getProfile().getRating())
                                    .avatar(associate.getProfile().getAvatar())
                                    .image(associate.getProfile().getCategory().getImage())
                                    .build()
                            )
                            .toList()
            );
        }
        return response;
    }
}