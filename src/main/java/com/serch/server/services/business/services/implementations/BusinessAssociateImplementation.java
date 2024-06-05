package com.serch.server.services.business.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.verified.ConsentType;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.account.Specialty;
import com.serch.server.models.auth.Pending;
import com.serch.server.models.business.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.models.business.BusinessSubscription;
import com.serch.server.models.email.Email;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.repositories.auth.PendingRepository;
import com.serch.server.repositories.business.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import com.serch.server.repositories.business.BusinessSubscriptionRepository;
import com.serch.server.services.account.requests.AddAssociateRequest;
import com.serch.server.services.account.services.AccountDeleteService;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.auth.services.JwtService;
import com.serch.server.services.business.responses.BusinessAssociateResponse;
import com.serch.server.services.business.services.BusinessAssociateService;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.ProviderAuthService;
import com.serch.server.services.email.services.EmailTemplateService;
import com.serch.server.services.rating.services.RatingService;
import com.serch.server.services.referral.services.ReferralProgramService;
import com.serch.server.services.referral.services.ReferralService;
import com.serch.server.utils.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final RatingService ratingService;
    private final ReferralService referralService;
    private final JwtService jwtService;
    private final ReferralProgramService referralProgramService;
    private final EmailTemplateService emailTemplateService;
    private final UserUtil util;
    private final PasswordEncoder passwordEncoder;
    private final BusinessProfileRepository businessProfileRepository;
    private final UserRepository userRepository;
    private final IncompleteRepository incompleteRepository;
    private final ProfileRepository profileRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PendingRepository pendingRepository;
    private final BusinessSubscriptionRepository businessSubscriptionRepository;

    @Override
    public BusinessAssociateResponse response(Profile profile) {
        BusinessAssociateResponse response = new BusinessAssociateResponse();
        response.setBad(ratingService.bad(String.valueOf(profile.getId())).getData());
        response.setGood(ratingService.good(String.valueOf(profile.getId())).getData());
        response.setChart(ratingService.chart(String.valueOf(profile.getId())).getData());
        response.setProfile(profileService.profile(profile));
        response.setStatus(profile.getUser().getAccountStatus());
        response.setVerified(profile.getUser().getIsEmailConfirmed());
        response.setSubscription(
                businessSubscriptionRepository.findByProfile_Id(profile.getId())
                        .map(BusinessSubscription::getStatus)
                        .orElse(AccountStatus.DEACTIVATED)
        );
        return response;
    }

    private List<BusinessAssociateResponse> associates() {
        BusinessProfile business = businessProfileRepository.findById(util.getUser().getId())
                .orElseThrow(() -> new AccountException("Business not found"));
        if(business.getAssociates() == null || business.getAssociates().isEmpty()) {
            return List.of();
        } else {
            return business.getAssociates().stream()
                    .filter(profile -> profile.getUser().getAccountStatus() != AccountStatus.BUSINESS_DELETED)
                    .sorted(Comparator.comparing(Profile::getCreatedAt))
                    .map(this::response)
                    .toList();
        }
    }

    @Override
    @Transactional
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
                    Profile profile = saveAssociateProfile(request, business, user);
                    saveAssociateSpecialties(request, profile);
                    saveAssociatePhoneInformation(request, user);
                    referralService.create(user, business.getUser());

                    sendEmailInvite(request.getEmailAddress(), business, user);
                    return new ApiResponse<>(
                            "Account created. Do inform the provider to confirm email when logging in",
                            associates(),
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

        Email email = new Email();
        email.setOtp("Click on this link to activate your account and start providing in the Serch platform");
        email.setGreeting("Welcome, %s".formatted(user.getFirstName()));
        email.setCentered(true);
        email.setLink(
                "https://www.serchservice.com/auth/associate/verify?invite=%s&role=%s&platform=%s"
                        .formatted(secret, user.getRole(), "provider")
        );
        email.setEmailAddress(emailAddress);
        email.setSubject("Hello %s, you were invited".formatted(user.getFirstName()));
        email.setContent(
                "%s, business account owned by %s, has invited you to the Serch Provider platform as "
                        .formatted(business.getBusinessName(), business.getUser().getFullName()) +
                        "someone who has the best skill needed to grow both the business and yourself. \n\n" +
                        "In order to access your account, you need to click on the link below to verify your " +
                        "identity as a person. \n\n" +
                        "Thanks and welcome!"
        );
        email.setHasLink(true);
        email.setReceiver(emailAddress);
        email.setImageHeader("/security.png?alt=media&token=9b7ce5b9-6353-4da9-b423-e73bf153eee3");
        emailTemplateService.sendEmail(email);
    }

    private User saveAssociateUser(AddAssociateRequest request, BusinessProfile business) {
        User user = new User();
        user.setEmailAddress(request.getEmailAddress());
        user.setPassword(business.getDefaultPassword());
        user.setEmailConfirmedAt(null);
        user.setRole(Role.ASSOCIATE_PROVIDER);
        user.setEmailConfirmedAt(LocalDateTime.now());
        user.setIsEmailConfirmed(false);
        user.setLastSignedIn(null);
        user.setAccountStatus(AccountStatus.BUSINESS_DEACTIVATED);
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
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setEmailAddress(request.getEmailAddress());
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
                provider.getUser().setAccountStatus(AccountStatus.BUSINESS_DELETED);
                provider.getUser().setUpdatedAt(LocalDateTime.now());
                userRepository.save(provider.getUser());
            }
            return new ApiResponse<>(
                    "Account is pending deletion. This takes time to be effected",
                    associates(),
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
            provider.getUser().setAccountStatus(AccountStatus.BUSINESS_DEACTIVATED);
            provider.getUser().setUpdatedAt(LocalDateTime.now());
            userRepository.save(provider.getUser());

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
                if(provider.getUser().getAccountStatus() == AccountStatus.BUSINESS_DEACTIVATED) {
                    provider.getUser().setAccountStatus(AccountStatus.ACTIVE);
                    provider.getUser().setUpdatedAt(LocalDateTime.now());

                    userRepository.save(provider.getUser());
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
    public ApiResponse<List<BusinessAssociateResponse>> all() {
        return new ApiResponse<>(associates());
    }
}
