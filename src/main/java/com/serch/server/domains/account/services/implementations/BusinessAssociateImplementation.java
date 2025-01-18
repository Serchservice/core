package com.serch.server.domains.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.email.EmailService;
import com.serch.server.core.jwt.JwtService;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.email.EmailType;
import com.serch.server.enums.verified.ConsentType;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.account.Profile;
import com.serch.server.models.account.Specialty;
import com.serch.server.models.auth.Pending;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.models.email.SendEmail;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.repositories.auth.PendingRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import com.serch.server.domains.account.requests.AddAssociateRequest;
import com.serch.server.domains.account.responses.BusinessAssociateResponse;
import com.serch.server.domains.account.services.AccountDeleteService;
import com.serch.server.domains.account.services.BusinessAssociateService;
import com.serch.server.domains.account.services.ProfileService;
import com.serch.server.domains.auth.services.AccountStatusTrackerService;
import com.serch.server.domains.auth.services.AuthService;
import com.serch.server.domains.auth.services.ProviderAuthService;
import com.serch.server.domains.referral.services.ReferralProgramService;
import com.serch.server.domains.referral.services.ReferralService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for managing business associates, including adding, deleting, deactivating,
 * and activating providers associated with a business.
 * <p></p>
 * This implements its wrapper class {@link BusinessAssociateService}
 *
 * @see AuthService
 * @see ProviderAuthService
 * @see AccountDeleteService
 * @see UserUtil
 * @see BusinessProfileRepository
 * @see UserRepository
 * @see IncompleteRepository
 * @see ProfileRepository
 */
@Service
@RequiredArgsConstructor
public class BusinessAssociateImplementation implements BusinessAssociateService {
    private final ProfileService profileService;
    private final AccountDeleteService deleteService;
    private final ReferralService referralService;
    private final JwtService jwtService;
    private final ReferralProgramService referralProgramService;
    private final EmailService emailService;
    private final AccountStatusTrackerService trackerService;
    private final UserUtil util;
    private final PasswordEncoder passwordEncoder;
    private final BusinessProfileRepository businessProfileRepository;
    private final UserRepository userRepository;
    private final IncompleteRepository incompleteRepository;
    private final ProfileRepository profileRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PendingRepository pendingRepository;

    @Value("${application.link.invite.associate}")
    private String ASSOCIATE_INVITE_LINK;

    @Override
    public BusinessAssociateResponse response(Profile profile) {
        BusinessAssociateResponse response = new BusinessAssociateResponse();
        response.setProfile(profileService.profile(profile));
        response.setStatus(profile.getUser().getStatus());
        response.setVerified(profile.getUser().getIsEmailConfirmed());

        return response;
    }

    private List<BusinessAssociateResponse> associates(String q, Integer page, Integer size) {
        Page<Profile> associates = q != null
                ? profileRepository.searchActiveAssociates(util.getUser().getId(), q, HelperUtil.getPageable(page, size))
                : profileRepository.findActiveAssociatesByBusinessId(util.getUser().getId(), HelperUtil.getPageable(page, size));

        if(associates == null || associates.getContent().isEmpty()) {
            return List.of();
        } else {
            return associates.getContent()
                    .stream()
                    .sorted(Comparator.comparing(Profile::getCreatedAt))
                    .map(this::response)
                    .toList();
        }
    }

    @Override
    public ApiResponse<List<BusinessAssociateResponse>> add(AddAssociateRequest request) {
        BusinessProfile business = businessProfileRepository.findById(util.getUser().getId())
                .orElseThrow(() -> new AccountException("Business not found"));

        if(request.getConsent() == ConsentType.YES) {
            Optional<User> existingUser = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());
            if(existingUser.isPresent()) {
                throw new AccountException("Email already exists");
            } else {
                Optional<Incomplete> incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress());
                if(incomplete.isPresent() && incomplete.get().hasProfile() && incomplete.get().hasCategory()) {
                    throw new AccountException("Email already exists");
                } else {
                    incomplete.ifPresent(incompleteRepository::delete);
                    User user = saveAssociateUser(request, business);
                    trackerService.create(user);
                    Profile profile = saveAssociateProfile(request, business, user);
                    saveAssociateSpecialties(request, profile);
                    saveAssociatePhoneInformation(request, user);
                    referralService.create(user, business.getUser());

                    sendEmailInvite(request.getEmailAddress(), business, user);

                    return new ApiResponse<>(
                            "Account created. Do inform the provider to confirm email when logging in",
                            associates(null, null, null),
                            HttpStatus.OK
                    );
                }
            }
        } else {
            throw new AccountException("You need to consent to your business being attached to this provider");
        }
    }

    @Override
    public ApiResponse<String> resendInvite(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AccountException("User does not exist"));
        BusinessProfile business = businessProfileRepository.findById(util.getUser().getId())
                .orElseThrow(() -> new AccountException("Business not found"));
        if(!user.getIsEmailConfirmed() && business.getAssociates() != null && !business.getAssociates().isEmpty() && business.getAssociates().stream().anyMatch(profile -> profile.isSameAs(user.getId()))) {
            sendEmailInvite(user.getEmailAddress(), business, user);

            return new ApiResponse<>("Email invite sent", HttpStatus.OK);
        } else {
            throw new AccountException("Cannot finish request. Check if user belongs to your business or user is already verified");
        }
    }

    private void sendEmailInvite(String emailAddress, BusinessProfile business, User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("business", business.getId());
        claims.put("user", user.getId());
        claims.put("role", user.getRole());
        String secret = jwtService.generateToken(claims, emailAddress);
        pendingRepository.findByUser_Id(user.getId())
                .ifPresentOrElse(pending -> {
                    pending.setSecret(passwordEncoder.encode(secret));
                    pendingRepository.save(pending);
                }, () -> {
                    Pending pending = new Pending();
                    pending.setUser(user);
                    pending.setSecret(passwordEncoder.encode(secret));
                    pendingRepository.save(pending);
                });

        SendEmail email = new SendEmail();
        email.setTo(emailAddress);
        email.setFirstName(user.getFirstName());
        email.setPrimary(business.getBusinessName());
        email.setSecondary(business.getUser().getFullName());
        email.setBusinessCategory(business.getCategory().getImage());
        email.setBusinessLogo(business.getBusinessLogo());
        email.setBusinessDescription(business.getBusinessDescription());
        email.setType(EmailType.ASSOCIATE_INVITE);
        email.setContent(String.format("%s?invite=%s&role=%s&platform=%s", ASSOCIATE_INVITE_LINK, secret, user.getRole(), "provider"));
        emailService.send(email);
    }

    private User saveAssociateUser(AddAssociateRequest request, BusinessProfile business) {
        User user = new User();
        user.setEmailAddress(request.getEmailAddress());
        user.setPassword(business.getDefaultPassword());
        user.setEmailConfirmedAt(null);
        user.setRole(Role.ASSOCIATE_PROVIDER);
        user.setEmailConfirmedAt(TimeUtil.now());
        user.setIsEmailConfirmed(false);
        user.setLastSignedIn(null);
        user.setStatus(AccountStatus.BUSINESS_DEACTIVATED);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        return userRepository.save(user);
    }

    private void saveAssociatePhoneInformation(AddAssociateRequest request, User user) {
        PhoneInformation phoneInformation = AccountMapper.INSTANCE.phoneInformation(request.getPhoneInfo());
        phoneInformation.setUser(user);
        phoneInformationRepository.save(phoneInformation);
    }

    private void saveAssociateSpecialties(AddAssociateRequest request, Profile profile) {
        if(!request.getSpecialties().isEmpty()) {
            request.getSpecialties().forEach(serviceKeyword -> {
                Specialty special = new Specialty();
                special.setSpecialty(serviceKeyword);
                special.setProfile(profile);
                specialtyRepository.save(special);
            });
        }
    }

    private Profile saveAssociateProfile(AddAssociateRequest request, BusinessProfile business, User saved) {
        referralProgramService.create(saved);

        Profile profile = new Profile();
        profile.setBusiness(business);
        profile.setCategory(request.getCategory());
        profile.setUser(saved);
        return profileRepository.save(profile);
    }

    @Override
    public ApiResponse<List<BusinessAssociateResponse>> delete(UUID id) {
        BusinessProfile business = businessProfileRepository.findById(util.getUser().getId())
                .orElseThrow(() -> new AccountException("Business not found"));
        Profile provider = profileRepository.findById(id)
                .orElseThrow(() -> new AccountException("Provider not found"));

        if(provider.belongsToBusiness(business.getId())) {
            ApiResponse<String> response = deleteService.delete(provider.getId());
            if(response.getStatus().is2xxSuccessful()) {
                provider.getUser().setStatus(AccountStatus.BUSINESS_DELETED);
                provider.getUser().setUpdatedAt(TimeUtil.now());
                userRepository.save(provider.getUser());
                trackerService.create(provider.getUser());
            }

            return new ApiResponse<>(
                    "Account is pending deletion. This takes time to be effected",
                    associates(null, null, null),
                    HttpStatus.OK
            );
        } else {
            throw new AccountException("Access denied. Provider does not belong to this business");
        }
    }

    @Override
    public ApiResponse<BusinessAssociateResponse> deactivate(UUID id) {
        BusinessProfile business = businessProfileRepository.findById(util.getUser().getId())
                .orElseThrow(() -> new AccountException("Business not found"));
        Profile provider = profileRepository.findById(id)
                .orElseThrow(() -> new AccountException("Provider not found"));

        if(provider.belongsToBusiness(business.getId())) {
            provider.getUser().setStatus(AccountStatus.BUSINESS_DEACTIVATED);
            provider.getUser().setUpdatedAt(TimeUtil.now());
            userRepository.save(provider.getUser());
            trackerService.create(provider.getUser());

            return new ApiResponse<>(
                    "Account deactivated. Provider won't be able to log in again",
                    response(provider),
                    HttpStatus.OK
            );
        } else {
            throw new AccountException("Access denied. Provider does not belong to this business");
        }
    }

    @Override
    public ApiResponse<BusinessAssociateResponse> activate(UUID id) {
        BusinessProfile business = businessProfileRepository.findById(util.getUser().getId())
                .orElseThrow(() -> new AccountException("Business not found"));
        Profile provider = profileRepository.findById(id)
                .orElseThrow(() -> new AccountException("Provider not found"));

        if(provider.belongsToBusiness(business.getId())) {
            if(provider.getUser().getIsEmailConfirmed()) {
                if(provider.getUser().getStatus() == AccountStatus.BUSINESS_DEACTIVATED) {
                    provider.getUser().setStatus(AccountStatus.ACTIVE);
                    provider.getUser().setUpdatedAt(TimeUtil.now());

                    userRepository.save(provider.getUser());
                    trackerService.create(provider.getUser());

                    return new ApiResponse<>(
                            "Account activated. Provider can log in to this account.",
                            response(provider),
                            HttpStatus.OK
                    );
                } else {
                    throw new AccountException(
                            "This account is not deactivated by the business administrator." +
                                    " Contact support for more information"
                    );
                }
            } else {
                throw new AccountException("You cannot change this account status until invite is accepted");
            }
        } else {
            throw new AccountException("Access denied. Provider does not belong to this business");
        }
    }

    @Override
    public ApiResponse<List<BusinessAssociateResponse>> all(String q, Integer page, Integer size) {
        return new ApiResponse<>(associates(q, page, size));
    }
}
