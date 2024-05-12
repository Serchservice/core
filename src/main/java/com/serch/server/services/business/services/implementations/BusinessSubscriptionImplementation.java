package com.serch.server.services.business.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.business.BusinessProfile;
import com.serch.server.models.business.BusinessSubscription;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.business.BusinessProfileRepository;
import com.serch.server.repositories.business.BusinessSubscriptionRepository;
import com.serch.server.services.business.responses.BusinessAssociateResponse;
import com.serch.server.services.business.services.BusinessAssociateService;
import com.serch.server.services.business.services.BusinessSubscriptionService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessSubscriptionImplementation implements BusinessSubscriptionService {
    private final BusinessAssociateService associateService;
    private final BusinessProfileRepository businessProfileRepository;
    private final BusinessSubscriptionRepository businessSubscriptionRepository;
    private final ProfileRepository profileRepository;

    @Override
    public ApiResponse<List<BusinessAssociateResponse>> subscriptions() {
        BusinessProfile business = businessProfileRepository.findByUser_EmailAddress(UserUtil.getLoginUser())
                .orElseThrow(() -> new AccountException("Business not found"));

        if(!business.getSubscriptions().isEmpty()) {
            return new ApiResponse<>(getSubscriptionList());
        } else {
            return new ApiResponse<>("No associate providers added to subscription");
        }
    }

    private List<BusinessAssociateResponse> getSubscriptionList() {
        BusinessProfile business = businessProfileRepository.findByUser_EmailAddress(UserUtil.getLoginUser())
                .orElseThrow(() -> new AccountException("Business not found"));

        return business.getSubscriptions()
                .stream()
                .sorted(Comparator.comparing(BusinessSubscription::getCreatedAt))
                .map(subscription -> {
                    BusinessAssociateResponse response = associateService.response(subscription.getProfile());
                    response.setSubscription(subscription.getStatus());
                    return response;
                })
                .toList();
    }

    @Override
    public ApiResponse<List<BusinessAssociateResponse>> add(UUID id) {
        BusinessProfile business = businessProfileRepository.findByUser_EmailAddress(UserUtil.getLoginUser())
                .orElseThrow(() -> new AccountException("Business not found"));
        Profile provider = profileRepository.findById(id).orElseThrow(() -> new AccountException("Provider not found"));

        if(provider.belongsToBusiness(business.getId())) {
            Optional<BusinessSubscription> sub = businessSubscriptionRepository.findByProfile_Id(id);
            if(sub.isPresent()) {
                return new ApiResponse<>("Provider is in your subscription list");
            } else {
                BusinessSubscription subscription = new BusinessSubscription();
                subscription.setProfile(provider);
                subscription.setBusiness(business);
                subscription.setStatus(AccountStatus.HAS_REPORTED_ISSUES);
                businessSubscriptionRepository.save(subscription);

                return new ApiResponse<>(getSubscriptionList());
            }
        } else {
            throw new AccountException("Provider does not belong to your business");
        }
    }

    @Override
    public ApiResponse<List<BusinessAssociateResponse>> addAll(List<UUID> ids) {
        BusinessProfile business = businessProfileRepository.findByUser_EmailAddress(UserUtil.getLoginUser())
                .orElseThrow(() -> new AccountException("Business not found"));
        List<Profile> providers = profileRepository.findAllById(ids);

        if(!providers.isEmpty()) {
            providers.forEach(provider -> {
                if(provider.belongsToBusiness(business.getId())) {
                    Optional<BusinessSubscription> sub = businessSubscriptionRepository.findByProfile_Id(provider.getId());
                    if(sub.isEmpty()) {
                        BusinessSubscription subscription = new BusinessSubscription();
                        subscription.setProfile(provider);
                        subscription.setBusiness(business);
                        subscription.setStatus(AccountStatus.HAS_REPORTED_ISSUES);
                        businessSubscriptionRepository.save(subscription);
                    }
                } else {
                    throw new AccountException("Provider does not belong to your business");
                }
            });
            return new ApiResponse<>(getSubscriptionList());
        } else {
            throw new AccountException("Provider does not belong to your business");
        }
    }

    @Override
    public ApiResponse<List<BusinessAssociateResponse>> suspend(UUID id) {
        BusinessProfile business = businessProfileRepository.findByUser_EmailAddress(UserUtil.getLoginUser())
                .orElseThrow(() -> new AccountException("Business not found"));
        Profile provider = profileRepository.findById(id).orElseThrow(() -> new AccountException("Provider not found"));

        if(provider.belongsToBusiness(business.getId())) {
            BusinessSubscription sub = businessSubscriptionRepository.findByProfile_Id(id)
                    .orElseThrow(() -> new AccountException("You need to add associate provider to the list before suspending the subscription"));

            sub.setStatus(AccountStatus.SUSPENDED);
            sub.setUpdatedAt(LocalDateTime.now());
            businessSubscriptionRepository.save(sub);
            return new ApiResponse<>(
                    "%s subscription is suspended".formatted(provider.getFullName()),
                    getSubscriptionList(),
                    HttpStatus.OK
            );
        } else {
            throw new AccountException("Provider does not belong to your business");
        }
    }

    @Override
    public ApiResponse<List<BusinessAssociateResponse>> remove(UUID id) {
        BusinessProfile business = businessProfileRepository.findByUser_EmailAddress(UserUtil.getLoginUser())
                .orElseThrow(() -> new AccountException("Business not found"));
        Profile provider = profileRepository.findById(id).orElseThrow(() -> new AccountException("Provider not found"));

        if(provider.belongsToBusiness(business.getId())) {
            BusinessSubscription sub = businessSubscriptionRepository.findByProfile_Id(id)
                    .orElseThrow(() -> new AccountException("You need to add associate provider to the list before suspending the subscription"));

            businessSubscriptionRepository.delete(sub);
            return new ApiResponse<>(
                    "%s is removed from your business subscription list".formatted(provider.getFullName()),
                    getSubscriptionList(),
                    HttpStatus.OK
            );
        } else {
            throw new AccountException("Provider does not belong to your business");
        }
    }
}