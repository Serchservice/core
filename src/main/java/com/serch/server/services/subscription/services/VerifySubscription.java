package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.exceptions.subscription.SubscriptionException;
import com.serch.server.mappers.SubscriptionMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.models.subscription.*;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import com.serch.server.repositories.subscription.*;
import com.serch.server.services.account.services.AdditionalService;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.account.services.SpecialtyService;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.ProviderAuthService;
import com.serch.server.services.payment.core.PaymentService;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import com.serch.server.services.subscription.requests.VerifySubscriptionRequest;
import com.serch.server.services.transaction.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * The VerifySubscription class implements the VerifySubscriptionService interface
 * and provides methods to verify subscription requests.
 * <p></p>
 * It interacts with payment services and repositories to verify subscription payments.
 *
 * @see VerifySubscriptionService
 */
@Service
@RequiredArgsConstructor
public class VerifySubscription implements VerifySubscriptionService {
    private final PaymentService paymentService;
    private final ProfileService profileService;
    private final SpecialtyService specialtyService;
    private final AdditionalService additionalService;
    private final AuthService authService;
    private final InvoiceService invoiceService;
    private final ProviderAuthService providerAuthService;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionAuthRepository subscriptionAuthRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;
    private final IncompleteRepository incompleteRepository;

    @Override
    public ApiResponse<String> verify(User user, String reference) {
        SubscriptionRequest request = subscriptionRequestRepository.findByReferenceAndEmailAddress(
                reference, user.getEmailAddress()
        ).orElseThrow(() -> new SubscriptionException("Subscription not found"));

        if(request.getParent().getType() == PlanType.FREE) {
            return verifyFree(request, user, null);
        } else {
            return verifyPaid(request, user, null);
        }
    }

    @Override
    public ApiResponse<String> verify(VerifySubscriptionRequest request) {
        SubscriptionRequest subRequest = subscriptionRequestRepository.findByReferenceAndEmailAddress(
                request.getReference(), request.getEmailAddress()
        ).orElseThrow(() -> new SubscriptionException("Subscription not found"));

        Incomplete incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new AuthException("User not found on registration"));

        ApiResponse<String> response = providerAuthService.checkStatus(request.getEmailAddress());
        if(response.getStatus().is2xxSuccessful()) {
            if(subRequest.getParent().getType() == PlanType.FREE) {
                return verifyFree(subRequest, null, incomplete);
            } else {
                return verifyPaid(subRequest, null, incomplete);
            }
        } else {
            return new ApiResponse<>(response.getMessage());
        }
    }

    /**
     * Verifies a paid subscription request. If the user is already registered,
     * updates the subscription details; otherwise, creates a new subscription.
     * @param request The subscription request to be verified.
     * @param user The user associated with the subscription request.
     * @param incomplete The incomplete profile information, if available.
     * @return An ApiResponse indicating the status of the verification process.
     */
    private ApiResponse<String> verifyPaid(SubscriptionRequest request, User user, Incomplete incomplete) {
        PaymentVerificationData data = paymentService.verify(request.getReference());

        if(user != null && incomplete == null) {
            Optional<Subscription> existing = subscriptionRepository.findByUser_Id(user.getId());
            if(existing.isPresent()) {
                existing.get().setPlan(request.getParent());
                existing.get().setChild(request.getChild());
                existing.get().setPlanStatus(PlanStatus.ACTIVE);
                if(existing.get().isNotSameAuth(data.getAuthorization().getSignature())) {
                    subscriptionAuthRepository.delete(existing.get().getAuth());

                    SubscriptionAuth auth = createAuth(request.getEmailAddress(), data);
                    auth.setSubscription(existing.get());
                    subscriptionAuthRepository.save(auth);
                }
                existing.get().setUpdatedAt(LocalDateTime.now());
                existing.get().setRetries(0);
                existing.get().setSubscribedAt(LocalDateTime.now());
                Subscription saved = subscriptionRepository.save(existing.get());

                invoiceService.createInvoice(saved, String.valueOf(data.getAmount()), "CARD", data.getReference());
                subscriptionRequestRepository.delete(request);
            } else {
                createSubscription(request, user, data);
            }
            return new ApiResponse<>("Success", HttpStatus.OK);
        } else if(user == null && incomplete != null) {
            User newUser = authService.getUserFromIncomplete(incomplete, Role.PROVIDER);
            ApiResponse<Profile> response = profileService.createProviderProfile(incomplete, newUser);
            if(response.getStatus().is2xxSuccessful()) {
                additionalService.saveIncompleteAdditional(incomplete, response.getData());
                specialtyService.saveIncompleteSpecialties(incomplete, response.getData());
                createSubscription(request, newUser, data);
                incompleteRepository.delete(incomplete);
                return new ApiResponse<>("Success", HttpStatus.OK);
            } else {
                return new ApiResponse<>(response.getMessage());
            }
        } else {
            throw new SubscriptionException("User or Incomplete profile is needed");
        }
    }

    /**
     * Creates a new subscription for a given user based on a subscription request.
     * @param request The subscription request.
     * @param user The user for whom the subscription is created.
     * @param data The payment verification data.
     */
    private void createSubscription(SubscriptionRequest request, User user, PaymentVerificationData data) {
        Subscription subscription = new Subscription();
        subscription.setPlan(request.getParent());
        subscription.setChild(request.getChild());
        subscription.setPlanStatus(PlanStatus.ACTIVE);
        subscription.setUpdatedAt(LocalDateTime.now());
        subscription.setUser(user);
        subscription.setSubscribedAt(LocalDateTime.now());
        Subscription subscribed = subscriptionRepository.save(subscription);

        SubscriptionAuth auth = createAuth(request.getEmailAddress(), data);
        auth.setSubscription(subscribed);
        subscriptionAuthRepository.save(auth);

        invoiceService.createInvoice(subscribed, String.valueOf(data.getAmount()), "CARD", data.getReference());

        subscriptionRequestRepository.delete(request);
    }

    @Override
    public SubscriptionAuth createAuth(String emailAddress, PaymentVerificationData response) {
        SubscriptionAuth auth = SubscriptionMapper.INSTANCE.auth(response.getAuthorization());
        auth.setEmailAddress(emailAddress);
        return auth;
    }

    /**
     * Verifies a free subscription request. If the user is already registered,
     * updates the subscription details; otherwise, creates a new subscription.
     * @param request The subscription request to be verified.
     * @param user The user associated with the subscription request.
     * @param incomplete The incomplete profile information, if available.
     * @return An ApiResponse indicating the status of the verification process.
     */
    private ApiResponse<String> verifyFree(SubscriptionRequest request, User user, Incomplete incomplete) {
        if(user != null && incomplete == null) {
            Optional<Subscription> existing = subscriptionRepository.findByUser_Id(user.getId());
            if(existing.isPresent()) {
                if(existing.get().canUseFreePlan()) {
                    existing.get().setPlan(request.getParent());
                    existing.get().setChild(request.getChild());
                    existing.get().setPlanStatus(PlanStatus.ACTIVE);
                    existing.get().setFreePlanStatus(PlanStatus.USED);
                    existing.get().setUpdatedAt(LocalDateTime.now());
                    existing.get().setSubscribedAt(LocalDateTime.now());
                    existing.get().setRetries(0);
                    Subscription saved = subscriptionRepository.save(existing.get());

                    invoiceService.createInvoice(saved, "", "WALLET", "");
                    subscriptionRequestRepository.delete(request);
                    return new ApiResponse<>("Success", HttpStatus.OK);
                } else {
                    throw new SubscriptionException("You cannot subscribe to free plan anymore");
                }
            } else {
                createSubscription(request, user);
                return new ApiResponse<>("Success", HttpStatus.OK);
            }
        } else if(user == null && incomplete != null) {
            User newUser = authService.getUserFromIncomplete(incomplete, Role.PROVIDER);
            ApiResponse<Profile> response = profileService.createProviderProfile(incomplete, newUser);
            if(response.getStatus().is2xxSuccessful()) {
                additionalService.saveIncompleteAdditional(incomplete, response.getData());
                specialtyService.saveIncompleteSpecialties(incomplete, response.getData());
                createSubscription(request, newUser);
                incompleteRepository.delete(incomplete);
                return new ApiResponse<>("Success", HttpStatus.OK);
            } else {
                return new ApiResponse<>(response.getMessage());
            }
        } else {
            throw new SubscriptionException("User or Incomplete profile is needed");
        }
    }

    private void createSubscription(SubscriptionRequest request, User user) {
        Subscription subscription = new Subscription();
        subscription.setPlan(request.getParent());
        subscription.setChild(request.getChild());
        subscription.setPlanStatus(PlanStatus.ACTIVE);
        subscription.setFreePlanStatus(PlanStatus.USED);
        subscription.setUpdatedAt(LocalDateTime.now());
        subscription.setUser(user);
        subscription.setSubscribedAt(LocalDateTime.now());
        Subscription subscribed = subscriptionRepository.save(subscription);
        subscriptionRequestRepository.delete(request);

        invoiceService.createInvoice(subscribed, "", "WALLET", "");
    }
}
