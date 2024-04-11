package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.verified.ConsentType;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import com.serch.server.services.account.requests.AddAssociateRequest;
import com.serch.server.services.account.services.AccountDeleteService;
import com.serch.server.services.account.services.AssociateService;
import com.serch.server.services.auth.requests.RequestProviderProfile;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.ProviderAuthService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssociateImplementation implements AssociateService {
    private final AuthService authService;
    private final ProviderAuthService providerAuthService;
    private final AccountDeleteService deleteService;
    private final UserUtil util;
    private final BusinessProfileRepository businessProfileRepository;
    private final UserRepository userRepository;
    private final IncompleteRepository incompleteRepository;
    private final ProfileRepository profileRepository;

    @Override
    public ApiResponse<String> add(AddAssociateRequest request) {
        BusinessProfile business = businessProfileRepository.findById(util.getUser().getId())
                .orElseThrow(() -> new AccountException("Business not found"));

        if(request.getConsent() == ConsentType.YES) {
            Optional<User> user = userRepository.findByEmailAddress(request.getEmailAddress());
            if(user.isPresent()) {
                throw new AccountException("Email already exists");
            } else {
                Optional<Incomplete> incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress());
                if(incomplete.isPresent()) {
                    throw new AccountException("Email already exists");
                } else {
                    Incomplete complete = authService.sendOtp(request.getEmailAddress());
                    complete.setRole(Role.ASSOCIATE_PROVIDER);
                    complete.setBusiness(business);
                    Incomplete saved = incompleteRepository.save(complete);

                    RequestProviderProfile profile = AccountMapper.INSTANCE.profile(request);
                    profile.setPassword(business.getDefaultPassword());
                    profile.setReferral(business.getReferralCode());
                    providerAuthService.saveProfile(profile, saved);
                    providerAuthService.saveCategory(request.getCategory(), saved);
                    providerAuthService.addSpecialtiesToIncompleteProfile(request.getSpecialties(), saved);

                    return new ApiResponse<>(
                            "Account created. Do inform the provider to confirm email when logging in",
                            HttpStatus.OK
                    );
                }
            }
        } else {
            throw new AccountException("You need to consent to your business being attached to this provider");
        }
    }

    @Override
    public ApiResponse<String> delete(UUID id) {
        BusinessProfile business = businessProfileRepository.findById(util.getUser().getId())
                .orElseThrow(() -> new AccountException("Business not found"));
        Profile provider = profileRepository.findById(id)
                .orElseThrow(() -> new AccountException("Provider not found"));

        if(provider.belongsToBusiness(business.getSerchId())) {
            ApiResponse<String> response = deleteService.delete(provider.getSerchId());
            if(response.getStatus().is2xxSuccessful()) {
                provider.getUser().setAccountStatus(AccountStatus.BUSINESS_DELETED);
                provider.getUser().setUpdatedAt(LocalDateTime.now());
                userRepository.save(provider.getUser());
            }
            return response;
        } else {
            throw new AccountException("Access denied. Provider does not belong to this business");
        }
    }

    @Override
    public ApiResponse<String> deactivate(UUID id) {
        BusinessProfile business = businessProfileRepository.findById(util.getUser().getId())
                .orElseThrow(() -> new AccountException("Business not found"));
        Profile provider = profileRepository.findById(id)
                .orElseThrow(() -> new AccountException("Provider not found"));

        if(provider.belongsToBusiness(business.getSerchId())) {
            provider.getUser().setAccountStatus(AccountStatus.BUSINESS_DEACTIVATED);
            provider.getUser().setUpdatedAt(LocalDateTime.now());
            userRepository.save(provider.getUser());

            return new ApiResponse<>(
                    "Account deactivated. Provider won't be able to log in again",
                    HttpStatus.OK
            );
        } else {
            throw new AccountException("Access denied. Provider does not belong to this business");
        }
    }

    @Override
    public ApiResponse<String> activate(UUID id) {
        BusinessProfile business = businessProfileRepository.findById(util.getUser().getId())
                .orElseThrow(() -> new AccountException("Business not found"));
        Profile provider = profileRepository.findById(id)
                .orElseThrow(() -> new AccountException("Provider not found"));

        if(provider.belongsToBusiness(business.getSerchId())) {
            if(provider.getUser().getAccountStatus() == AccountStatus.BUSINESS_DEACTIVATED) {
                provider.getUser().setAccountStatus(AccountStatus.ACTIVE);
                provider.getUser().setUpdatedAt(LocalDateTime.now());

                userRepository.save(provider.getUser());
                return new ApiResponse<>(
                        "Account activated. Provider can log in to this account.",
                        HttpStatus.OK
                );
            } else {
                throw new AccountException(
                        "This account is not deactivated by the business administrator." +
                                " Contact support for more information"
                );
            }
        } else {
            throw new AccountException("Access denied. Provider does not belong to this business");
        }
    }
}
