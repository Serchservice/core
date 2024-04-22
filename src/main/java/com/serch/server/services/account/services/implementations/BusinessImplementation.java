package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.models.subscription.SubscriptionInvoice;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.repositories.subscription.SubscriptionInvoiceRepository;
import com.serch.server.services.account.requests.UpdateProfileRequest;
import com.serch.server.services.account.responses.BusinessProfileResponse;
import com.serch.server.services.account.responses.MoreProfileData;
import com.serch.server.services.account.responses.ProfileResponse;
import com.serch.server.services.account.services.BusinessService;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.referral.services.ReferralService;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.company.services.SpecialtyKeywordService;
import com.serch.server.services.storage.core.StorageService;
import com.serch.server.services.storage.requests.UploadRequest;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing business profiles, including creation, updating, and retrieval.
 * It implements the wrapper class {@link BusinessService}
 *
 * @see StorageService
 * @see ReferralService
 * @see WalletService
 * @see SpecialtyKeywordService
 * @see TokenService
 * @see UserUtil
 * @see BusinessProfileRepository
 * @see PhoneInformationRepository
 * @see UserRepository
 * @see SpecialtyRepository
 * @see ShopRepository
 * @see RatingRepository
 * @see SharedLinkRepository
 * @see SubscriptionInvoiceRepository
 */
@Service
@RequiredArgsConstructor
public class BusinessImplementation implements BusinessService {
    private final TokenService tokenService;
    private final ReferralService referralService;
    private final WalletService walletService;
    private final ProfileService profileService;
    private final StorageService storageService;
    private final SpecialtyKeywordService keywordService;
    private final UserUtil userUtil;
    private final BusinessProfileRepository businessProfileRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final RatingRepository ratingRepository;
    private final SharedLinkRepository sharedLinkRepository;
    private final SpecialtyRepository specialtyRepository;
    private final SubscriptionInvoiceRepository subscriptionInvoiceRepository;

    @Value("${application.account.duration}")
    private Integer ACCOUNT_DURATION;

    @Override
    public ApiResponse<String> createProfile(Incomplete incomplete, User user) {
        BusinessProfile businessProfile = saveBusinessProfile(incomplete, user);
        savePhoneInformation(incomplete, user);
        if(incomplete.getReferredBy() != null) {
            referralService.create(user, incomplete.getReferredBy().getReferredBy());
        }
        walletService.createWallet(businessProfile.getUser());
        return new ApiResponse<>("Success", HttpStatus.OK);
    }

    private void savePhoneInformation(Incomplete incomplete, User user) {
        PhoneInformation phoneInformation = AccountMapper.INSTANCE.phoneInformation(
                incomplete.getPhoneInfo()
        );
        phoneInformation.setUser(user);
        phoneInformationRepository.save(phoneInformation);
    }

    private BusinessProfile saveBusinessProfile(Incomplete incomplete, User user) {
        String defaultPassword = "@%s%s".formatted(
                incomplete.getProfile().getBusinessName().toUpperCase(),
                tokenService.generateCode(2)
        );

        BusinessProfile businessProfile = AccountMapper.INSTANCE.profile(incomplete.getProfile());
        businessProfile.setUser(user);
        businessProfile.setEmailAddress(user.getEmailAddress());
        businessProfile.setCategory(incomplete.getCategory().getCategory());
        businessProfile.setDefaultPassword(defaultPassword);
        return businessProfileRepository.save(businessProfile);
    }

    @Override
    public ApiResponse<List<ProfileResponse>> associates() {
        BusinessProfile profile = businessProfileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new AccountException("Profile not found"));
        return new ApiResponse<>(
                "Success",
                profile.getAssociates()
                        .stream()
                        .map(profileService::profile)
                        .toList(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<List<ProfileResponse>> subscribedAssociates() {
        SubscriptionInvoice invoice = subscriptionInvoiceRepository
                .findBySubscription_PlanStatusAndSubscription_User_Id(PlanStatus.ACTIVE, userUtil.getUser().getId())
                .orElse(new SubscriptionInvoice());

        return new ApiResponse<>(
                "Success",
                invoice.getAssociates()
                        .stream()
                        .map(subscriptionAssociate -> profileService.profile(subscriptionAssociate.getProfile()))
                        .toList(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<BusinessProfileResponse> profile() {
        BusinessProfile profile = businessProfileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new AccountException("Profile not found"));

        BusinessProfileResponse response = AccountMapper.INSTANCE.profile(profile);
        response.setCertificate("");
        response.setVerificationStatus(VerificationStatus.NOT_VERIFIED);
        PhoneInformation phoneInformation = phoneInformationRepository.findByUser_Id(profile.getId())
                .orElse(new PhoneInformation());
        response.setPhoneInfo(AccountMapper.INSTANCE.phoneInformation(phoneInformation));
        response.setBusinessInformation(AccountMapper.INSTANCE.business(profile));
        response.setSpecializations(
                specialtyRepository.findByProfile_Business_Id(profile.getId())
                        .stream()
                        .map(specialty -> keywordService.getSpecialtyResponse(specialty.getService()))
                        .toList()
        );

        MoreProfileData more = profileService.moreInformation(profile.getUser());
        more.setNumberOfRating(
                profile.getAssociates()
                        .stream()
                        .mapToInt(profile1 -> ratingRepository.findByRated(String.valueOf(profile1.getId())).size())
                        .sum()
        );
        more.setNumberOfShops(shopRepository.findByUser_Id(profile.getUser().getId()).size());
        more.setTotalShared(
                profile.getAssociates().stream()
                        .mapToInt(profile1 -> sharedLinkRepository.findByUserId(profile1.getId()).size())
                        .sum()
        );
        more.setTotalServiceTrips(0);
        response.setMore(more);
        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<String> update(UpdateProfileRequest request) {
        User user = userUtil.getUser();
        BusinessProfile profile = businessProfileRepository.findById(user.getId())
                .orElseThrow(() -> new AccountException("Profile not found"));
        if(user.isProfile()) {
            throw new AccountException("Access denied. Cannot perform action");
        } else {
            Duration duration = Duration.between(user.getLastUpdatedAt(), LocalDateTime.now());
            long remaining = ACCOUNT_DURATION - duration.toDays();

            if(remaining < 0) {
                updateLastName(request, profile);
                updateFirstName(request, profile);
                profileService.updatePhoneInformation(request, user);
                if(!request.getAvatar().isEmpty()) {
                    return updateAvatar(request, profile);
                }
                return new ApiResponse<>("Update successful", HttpStatus.OK);
            } else {
                throw new AccountException("You can update your profile in the next %s days".formatted(remaining));
            }
        }
    }

    private ApiResponse<String> updateAvatar(UpdateProfileRequest request, BusinessProfile profile) {
        UploadRequest upload = new UploadRequest();
        upload.setFile(request.getAvatar());

        ApiResponse<String> response = storageService.upload(upload);
        if(response.getStatus().is2xxSuccessful()) {
            profile.setAvatar(response.getData());
            updateTimeStamps(profile.getUser(), profile);
        }
        return response;
    }

    private void updateFirstName(UpdateProfileRequest request, BusinessProfile profile) {
        boolean canUpdateFirstName = request.getFirstName() != null
                && !request.getFirstName().isEmpty()
                && !profile.getUser().getFirstName().equalsIgnoreCase(request.getFirstName());
        if(canUpdateFirstName) {
            profile.getUser().setFirstName(request.getFirstName());
            profile.setFirstName(request.getFirstName());
            updateTimeStamps(profile.getUser(), profile);
        }
    }

    private void updateLastName(UpdateProfileRequest request, BusinessProfile profile) {
        boolean canUpdateLastName = request.getLastName() != null
                && !request.getLastName().isEmpty()
                && !profile.getUser().getLastName().equalsIgnoreCase(request.getLastName());
        if(canUpdateLastName) {
            profile.getUser().setLastName(request.getLastName());
            profile.setLastName(request.getLastName());
            updateTimeStamps(profile.getUser(), profile);
        }
    }

    private void updateTimeStamps(User user, BusinessProfile profile) {
        user.setUpdatedAt(LocalDateTime.now());
        user.setLastUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        profile.setUpdatedAt(LocalDateTime.now());
        businessProfileRepository.save(profile);
    }
}
