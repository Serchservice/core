package com.serch.server.domains.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.file.services.FileService;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.domains.account.services.SpecialtyService;
import com.serch.server.domains.account.requests.UpdateBusinessRequest;
import com.serch.server.domains.account.responses.BusinessProfileResponse;
import com.serch.server.domains.account.responses.MoreProfileData;
import com.serch.server.domains.account.services.BusinessService;
import com.serch.server.domains.account.services.ProfileService;
import com.serch.server.domains.auth.requests.RequestBusinessProfile;
import com.serch.server.domains.referral.services.ReferralService;
import com.serch.server.core.token.TokenService;
import com.serch.server.domains.transaction.services.WalletService;
import com.serch.server.utils.DatabaseUtil;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * Service for managing business profiles, including creation, updating, and retrieval.
 * It implements the wrapper class {@link BusinessService}
 *
 * @see ReferralService
 * @see WalletService
 * @see SpecialtyService
 * @see TokenService
 * @see AuthUtil
 * @see BusinessProfileRepository
 * @see PhoneInformationRepository
 * @see UserRepository
 * @see SpecialtyRepository
 * @see ShopRepository
 * @see RatingRepository
 * @see SharedLinkRepository
 */
@Service
@RequiredArgsConstructor
public class BusinessImplementation implements BusinessService {
    private final TokenService tokenService;
    private final ReferralService referralService;
    private final WalletService walletService;
    private final ProfileService profileService;
    private final FileService uploadService;
    private final SpecialtyService specialtyService;
    private final AuthUtil authUtil;
    private final BusinessProfileRepository businessProfileRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final RatingRepository ratingRepository;
    private final SharedLinkRepository sharedLinkRepository;
    private final SpecialtyRepository specialtyRepository;
    private final TripRepository tripRepository;

    @Value("${application.account.duration}")
    private Integer ACCOUNT_DURATION;

    @Override
    public ApiResponse<String> createProfile(Incomplete incomplete, User user, RequestBusinessProfile profile) {
        BusinessProfile businessProfile = saveProfile(incomplete, user, profile);
        savePhoneInformation(incomplete, user);

        if(incomplete.getReferredBy() != null) {
            referralService.create(user, incomplete.getReferredBy().getReferredBy());
        }

        walletService.create(businessProfile.getUser());
        return new ApiResponse<>("Success", HttpStatus.OK);
    }

    private void savePhoneInformation(Incomplete incomplete, User user) {
        PhoneInformation phoneInformation = AccountMapper.INSTANCE.phoneInformation(incomplete.getPhoneInfo());
        phoneInformation.setUser(user);
        phoneInformationRepository.save(phoneInformation);
    }

    private BusinessProfile saveProfile(Incomplete incomplete, User user, RequestBusinessProfile profile) {
        String defaultPassword = "@%s%s".formatted(profile.getName().toUpperCase(), tokenService.generateCode(2));

        BusinessProfile business = AccountMapper.INSTANCE.profile(incomplete.getProfile());
        business.setUser(user);
        business.setCategory(incomplete.getCategory().getCategory());
        business.setDefaultPassword(DatabaseUtil.encode(defaultPassword));
        business.setBusinessAddress(profile.getAddress());
        business.setBusinessName(profile.getName());
        business.setBusinessDescription(profile.getDescription());
        business.setContact(profile.getContact());
        business.setLongitude(profile.getLongitude() != null ? profile.getLongitude() : 0.0);
        business.setLatitude(profile.getLatitude() != null ? profile.getLatitude() : 0.0);
        business.setPlace(profile.getPlace() != null ? profile.getPlace() : profile.getAddress());

        return businessProfileRepository.save(business);
    }

    @Override
    public ApiResponse<BusinessProfileResponse> profile() {
        BusinessProfile profile = businessProfileRepository.findById(authUtil.getUser().getId())
                .orElseThrow(() -> new AccountException("Profile not found"));

        BusinessProfileResponse response = AccountMapper.INSTANCE.profile(profile);
        response.setVerificationStatus(VerificationStatus.NOT_VERIFIED);
        response.setFirstName(profile.getUser().getFirstName());
        response.setLastName(profile.getUser().getLastName());
        response.setDefaultPassword(DatabaseUtil.decodeData(profile.getDefaultPassword()));
        response.setEmailAddress(profile.getUser().getEmailAddress());

        addPhoneInformation(profile, response);

        response.setSpecializations(
                specialtyRepository.findByProfile_Business_Id(profile.getId()) != null
                        ? specialtyRepository.findByProfile_Business_Id(profile.getId())
                        .stream()
                        .map(specialtyService::response)
                        .toList()
                        : List.of()
        );
        response.setCategory(profile.getCategory().getType());
        response.setImage(profile.getCategory().getImage());

        addMoreProfileInformation(profile, response);
        return new ApiResponse<>(response);
    }

    private void addPhoneInformation(BusinessProfile profile, BusinessProfileResponse response) {
        PhoneInformation phoneInformation = phoneInformationRepository.findByUser_Id(profile.getId())
                .orElse(new PhoneInformation());
        response.setPhoneInfo(AccountMapper.INSTANCE.phoneInformation(phoneInformation));
    }

    private void addMoreProfileInformation(BusinessProfile profile, BusinessProfileResponse response) {
        MoreProfileData more = profileService.moreInformation(profile.getUser());
        more.setNumberOfRating(
                profile.getAssociates() != null
                        ? profile.getAssociates()
                        .stream()
                        .mapToInt(profile1 -> ratingRepository.findByRated(String.valueOf(profile1.getId())).size())
                        .sum()
                        : 0
        );
        more.setNumberOfShops(shopRepository.findByUser_Id(profile.getUser().getId()).size());
        more.setTotalShared(
                profile.getAssociates() != null
                        ? profile.getAssociates().stream()
                        .mapToInt(profile1 -> sharedLinkRepository.findByUserId(profile1.getId()).size())
                        .sum()
                        : 0
        );
        more.setTotalServiceTrips(
                profile.getAssociates() != null
                        ? profile.getAssociates().stream()
                        .mapToInt(provider -> tripRepository.findByProviderId(provider.getId()).size())
                        .sum()
                        : 0
        );
        response.setMore(more);
    }

    @Override
    public ApiResponse<BusinessProfileResponse> update(UpdateBusinessRequest request) {
        User user = authUtil.getUser();
        BusinessProfile profile = businessProfileRepository.findById(user.getId())
                .orElseThrow(() -> new AccountException("Profile not found"));

        if(user.isProfile()) {
            throw new AccountException("Access denied. Cannot perform action");
        } else if(user.getProfileLastUpdatedAt() == null) {
            return getBusinessUpdateResponse(request, user, profile);
        } else {
            Duration duration = Duration.between(user.getProfileLastUpdatedAt(), TimeUtil.now());
            long remaining = ACCOUNT_DURATION - duration.toDays();

            if(remaining < 0) {
                return getBusinessUpdateResponse(request, user, profile);
            } else {
                throw new AccountException("You can update your profile in the next %s days".formatted(remaining));
            }
        }
    }

    protected ApiResponse<BusinessProfileResponse> getBusinessUpdateResponse(UpdateBusinessRequest request, User user, BusinessProfile profile) {
        updateLastName(request, profile);
        updateFirstName(request, profile);
        updateBusinessAddress(request, profile);
        updateBusinessContact(request, profile);
        updateBusinessName(request, profile);
        updateBusinessDescription(request, profile);
        updateGender(request, profile);

        profileService.updatePhoneInformation(request.getPhone(), user);
        if(!HelperUtil.isUploadEmpty(request.getUpload())) {
            String url = uploadService.uploadCommon(request.getUpload(), user).getFile();
            profile.setAvatar(url);
            updateTimeStamps(profile.getUser(), profile);
        }
        if(!HelperUtil.isUploadEmpty(request.getLogo())) {
            String url = uploadService.uploadCommon(request.getLogo(), user).getFile();
            profile.setBusinessLogo(url);
            updateTimeStamps(profile.getUser(), profile);
        }
        return profile();
    }

    private void updateFirstName(UpdateBusinessRequest request, BusinessProfile profile) {
        boolean canUpdateFirstName = request.getFirstName() != null
                && !request.getFirstName().isEmpty()
                && !profile.getUser().getFirstName().equalsIgnoreCase(request.getFirstName());

        if(canUpdateFirstName) {
            profile.getUser().setFirstName(request.getFirstName());
            updateTimeStamps(profile.getUser(), profile);
        }
    }

    private void updateLastName(UpdateBusinessRequest request, BusinessProfile profile) {
        boolean canUpdateLastName = request.getLastName() != null
                && !request.getLastName().isEmpty()
                && !profile.getUser().getLastName().equalsIgnoreCase(request.getLastName());

        if(canUpdateLastName) {
            profile.getUser().setLastName(request.getLastName());
            updateTimeStamps(profile.getUser(), profile);
        }
    }

    private void updateGender(UpdateBusinessRequest request, BusinessProfile profile) {
        if(request.getGender() != null && profile.getGender() != request.getGender()) {
            profile.setGender(request.getGender());
            profile.setUpdatedAt(TimeUtil.now());
            businessProfileRepository.save(profile);
        }
    }

    private void updateBusinessName(UpdateBusinessRequest request, BusinessProfile profile) {
        boolean canUpdateBusinessName = request.getBusinessName() != null
                && !request.getBusinessName().isEmpty()
                && !profile.getBusinessName().equalsIgnoreCase(request.getBusinessName());

        if(canUpdateBusinessName) {
            profile.setBusinessName(request.getBusinessName());
            profile.setUpdatedAt(TimeUtil.now());
            businessProfileRepository.save(profile);
        }
    }

    private void updateBusinessAddress(UpdateBusinessRequest request, BusinessProfile profile) {
        boolean canUpdateBusinessAddress = request.getBusinessAddress() != null
                && !request.getBusinessAddress().isEmpty()
                && !profile.getBusinessAddress().equalsIgnoreCase(request.getBusinessAddress());

        if(canUpdateBusinessAddress) {
            profile.setBusinessAddress(request.getBusinessAddress());
            profile.setUpdatedAt(TimeUtil.now());
            businessProfileRepository.save(profile);
        }
    }

    private void updateBusinessContact(UpdateBusinessRequest request, BusinessProfile profile) {
        boolean canUpdateBusinessContact = request.getBusinessContact() != null
                && !request.getBusinessContact().isEmpty()
                && !profile.getContact().equalsIgnoreCase(request.getBusinessContact());

        if(canUpdateBusinessContact) {
            profile.setContact(request.getBusinessContact());
            profile.setUpdatedAt(TimeUtil.now());
            businessProfileRepository.save(profile);
        }
    }

    private void updateBusinessDescription(UpdateBusinessRequest request, BusinessProfile profile) {
        boolean canUpdateBusinessDescription = request.getBusinessDescription() != null
                && !request.getBusinessDescription().isEmpty()
                && !profile.getBusinessDescription().equalsIgnoreCase(request.getBusinessDescription());

        if(canUpdateBusinessDescription) {
            profile.setBusinessDescription(request.getBusinessDescription());
            profile.setUpdatedAt(TimeUtil.now());
            businessProfileRepository.save(profile);
        }
    }

    private void updateTimeStamps(User user, BusinessProfile profile) {
        user.setProfileLastUpdatedAt(TimeUtil.now());
        userRepository.save(user);

        profile.setUpdatedAt(TimeUtil.now());
        businessProfileRepository.save(profile);
    }
}
