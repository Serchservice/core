package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.exceptions.subscription.SubscriptionException;
import com.serch.server.mappers.SubscriptionMapper;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.models.subscription.*;
import com.serch.server.repositories.account.BusinessProfileRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerifySubscription implements VerifySubscriptionService {
    private final PaymentService paymentService;
    private final ProfileService profileService;
    private final SpecialtyService specialtyService;
    private final AdditionalService additionalService;
    private final AuthService authService;
    private final ProviderAuthService providerAuthService;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionAuthRepository subscriptionAuthRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;
    private final IncompleteRepository incompleteRepository;
    private final SubscriptionInvoiceRepository subscriptionInvoiceRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final SubscriptionAssociateRepository subscriptionAssociateRepository;

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

                createInvoice(saved, String.valueOf(data.getAmount()), "CARD", data.getReference());
                subscriptionRequestRepository.delete(request);
            } else {
                createSubscription(request, user, data);
            }
            return new ApiResponse<>("Success", HttpStatus.OK);
        } else if(user == null && incomplete != null) {
            User newUser = authService.getUserFromIncomplete(incomplete, Role.PROVIDER);
            ApiResponse<Profile> response = profileService.createProviderProfile(incomplete, newUser);
            if(response.getStatus().is2xxSuccessful()) {
                additionalService.saveIncompleteAdditional(incomplete, response);
                specialtyService.saveIncompleteSpecialties(incomplete, response);
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

        createInvoice(subscribed, String.valueOf(data.getAmount()), "CARD", data.getReference());

        subscriptionRequestRepository.delete(request);
    }

    @Override
    public void createInvoice(Subscription subscription, String amount, String mode, String reference) {
        SubscriptionInvoice invoice = new SubscriptionInvoice();

        if(subscription.getUser().isProfile()) {
            invoice.setSize(1);
        } else {
            BusinessProfile profile = businessProfileRepository.findById(subscription.getUser().getId())
                    .orElseThrow(() -> new SubscriptionException("Business not found"));
            invoice.setSize(
                    profile.getAssociates().stream()
                            .filter(sub -> !sub.getUser().isBusinessLocked())
                            .toList()
                            .size()
            );
        }
        invoice.setSubscription(subscription);
        invoice.setAmount(amount);
        invoice.setReference(reference);
        invoice.setMode(mode);
        invoice.setPlan(
                subscription.getChild() != null
                        ? subscription.getChild().getName()
                        : subscription.getPlan().getType().getType()
        );
        SubscriptionInvoice savedInvoice = subscriptionInvoiceRepository.save(invoice);

        if(!subscription.getUser().isProfile()) {
            BusinessProfile business = businessProfileRepository.findById(subscription.getUser().getId())
                    .orElseThrow(() -> new SubscriptionException("Business not found"));
            business.getAssociates()
                    .stream()
                    .filter(sub -> !sub.getUser().isBusinessLocked())
                    .forEach(profile -> {
                        SubscriptionAssociate associate = new SubscriptionAssociate();
                        associate.setInvoice(savedInvoice);
                        associate.setBusiness(business);
                        associate.setProfile(profile);
                        subscriptionAssociateRepository.save(associate);
                    });
        }
    }

    @Override
    public SubscriptionAuth createAuth(String emailAddress, PaymentVerificationData response) {
        SubscriptionAuth auth = SubscriptionMapper.INSTANCE.auth(response.getAuthorization());
        auth.setEmailAddress(emailAddress);
        return auth;
    }

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

                    createInvoice(saved, "", "WALLET", "");
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
                additionalService.saveIncompleteAdditional(incomplete, response);
                specialtyService.saveIncompleteSpecialties(incomplete, response);
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

        createInvoice(subscribed, "", "WALLET", "");
    }
}
