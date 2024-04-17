package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.mappers.AuthMapper;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.services.account.requests.RequestCreateProfile;
import com.serch.server.services.account.requests.UpdateProfileRequest;
import com.serch.server.services.account.responses.MoreProfileData;
import com.serch.server.services.account.responses.ProfileResponse;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.account.services.ReferralService;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.company.services.SpecialtyKeywordService;
import com.serch.server.services.storage.core.StorageService;
import com.serch.server.services.storage.requests.UploadRequest;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Service for managing user profiles, including creation, updating, and retrieval.
 * It implements the wrapper class {@link ProfileService}
 *
 * @see StorageService
 * @see ReferralService
 * @see WalletService
 * @see UserUtil
 * @see ProfileRepository
 * @see PhoneInformationRepository
 * @see UserRepository
 * @see SpecialtyRepository
 * @see ShopRepository
 * @see RatingRepository
 * @see SharedLinkRepository
 */
@Service
@RequiredArgsConstructor
public class ProfileImplementation implements ProfileService {
    private final StorageService storageService;
    private final ReferralService referralService;
    private final WalletService walletService;
    private final SpecialtyKeywordService keywordService;
    private final UserUtil userUtil;
    private final ProfileRepository profileRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final ShopRepository shopRepository;
    private final RatingRepository ratingRepository;
    private final SharedLinkRepository sharedLinkRepository;

    @Value("${application.account.duration}")
    private Integer ACCOUNT_DURATION;

    @Override
    public ApiResponse<Profile> createProfile(RequestCreateProfile request) {
        var user = profileRepository.findById(request.getUser().getId());
        if(user.isPresent()) {
            throw new AccountException("User already have a profile");
        } else {
            String referLink = HelperUtil.generateReferralLink(
                    request.getProfile().getFirstName(),
                    request.getProfile().getLastName(),
                    request.getCategory()
            );

            Profile profile = getProfile(request, referLink);
            PhoneInformation phoneInformation = AccountMapper.INSTANCE.phoneInformation(
                    request.getProfile().getPhoneInformation()
            );
            phoneInformation.setUser(request.getUser());
            phoneInformationRepository.save(phoneInformation);

            if(request.getReferredBy() != null) {
                referralService.create(request.getUser(), request.getReferredBy());
            }

            walletService.createWallet(profile.getUser());
            return new ApiResponse<>("Profile successfully saved", profile, HttpStatus.CREATED);
        }
    }

    @Override
    public ApiResponse<Profile> createProviderProfile(Incomplete incomplete, User user) {
        RequestCreateProfile createProfile = new RequestCreateProfile();
        createProfile.setUser(user);
        createProfile.setProfile(getRequestProfile(incomplete));
        createProfile.setCategory(incomplete.getCategory().getCategory());
        createProfile.setReferredBy(incomplete.getReferredBy().getReferredBy());
        return createProfile(createProfile);
    }

    @Override
    public ApiResponse<Profile> createUserProfile(RequestProfile request, User user, User referral) {
        RequestCreateProfile createProfile = new RequestCreateProfile();
        createProfile.setUser(user);
        createProfile.setProfile(request);
        createProfile.setCategory(SerchCategory.USER);
        createProfile.setReferredBy(referral);
        return createProfile(createProfile);
    }

    private RequestProfile getRequestProfile(Incomplete incomplete) {
        RequestProfile profile = AuthMapper.INSTANCE.profile(incomplete.getProfile());
        profile.setPassword(incomplete.getProfile().getPassword());
        profile.setEmailAddress(incomplete.getEmailAddress());
        profile.setPhoneInformation(AuthMapper.INSTANCE.phoneInformation(incomplete.getPhoneInfo()));
        return profile;
    }

    private Profile getProfile(RequestCreateProfile request, String referLink) {
        Profile profile = AccountMapper.INSTANCE.profile(request.getProfile());
        profile.setUser(request.getUser());
        profile.setReferLink(referLink);
        profile.setCategory(request.getCategory());
        profile.setReferralCode(HelperUtil.extractReferralCode(referLink));
        return profileRepository.save(profile);
    }

    @Override
    public ApiResponse<ProfileResponse> profile() {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new AccountException("Profile not found"));

        return new ApiResponse<>(profile(profile));
    }

    @Override
    public ProfileResponse profile(Profile profile) {
        ProfileResponse response = AccountMapper.INSTANCE.profile(profile);
        response.setCertificate("");
        response.setStatus("");
        response.setVerificationStatus(VerificationStatus.NOT_VERIFIED);

        if(profile.isAssociate()) {
            response.setBusinessInformation(AccountMapper.INSTANCE.business(profile.getBusiness()));
        }

        PhoneInformation phoneInformation = phoneInformationRepository.findByUser_Id(profile.getId())
                .orElse(new PhoneInformation());
        response.setPhoneInfo(AccountMapper.INSTANCE.phoneInformation(phoneInformation));

        MoreProfileData more = moreInformation(profile.getUser());
        more.setNumberOfShops(shopRepository.findByUser_Id(profile.getUser().getId()).size());
        more.setNumberOfRating(ratingRepository.findByRated(String.valueOf(profile.getId())).size());
        more.setTotalShared(sharedLinkRepository.findByUserId(profile.getId()).size());
        more.setTotalServiceTrips(0);
        response.setMore(more);

        response.setSpecializations(
                specialtyRepository.findByProfile_Id(profile.getId())
                        .stream()
                        .map(specialty -> keywordService.getSpecialtyResponse(specialty.getService()))
                        .toList()
        );
        return response;
    }

    @Override
    public MoreProfileData moreInformation(User user) {
        MoreProfileData more = new MoreProfileData();
        more.setIsEnabled(user.isEnabled());
        more.setIsNonLocked(user.isAccountNonLocked());
        more.setIsNonExpired(user.isAccountNonExpired());
        more.setLastSignedIn(TimeUtil.formatLastSignedIn(user.getLastSignedIn()));
        return more;
    }

    @Override
    public ApiResponse<String> update(UpdateProfileRequest request) {
        User user = userUtil.getUser();
        Profile profile = profileRepository.findById(user.getId())
                .orElseThrow(() -> new AccountException("Profile not found"));
        if(user.isProfile()) {
            Duration duration = Duration.between(user.getLastUpdatedAt(), LocalDateTime.now());
            long remaining = ACCOUNT_DURATION - duration.toDays();

            if(remaining < 0) {
                updateLastName(request, profile);
                updateFirstName(request, profile);
                updatePhoneInformation(request, user);
                if(!request.getAvatar().isEmpty()) {
                    return updateAvatar(request, profile);
                }
                return new ApiResponse<>("Update successful", HttpStatus.OK);
            } else {
                throw new AccountException("You can update your profile in the next %s days".formatted(remaining));
            }
        } else {
            throw new AccountException("Access denied. Cannot perform action");
        }
    }

    @Override
    public void updatePhoneInformation(UpdateProfileRequest request, User user) {
        if(request.getPhone() != null) {
            phoneInformationRepository.findByUser_Id(user.getId())
                    .ifPresentOrElse(phone -> {
                        if(!phone.getPhoneNumber().equalsIgnoreCase(request.getPhone().getPhoneNumber())) {
                            phone.setPhoneNumber(request.getPhone().getPhoneNumber());
                        }
                        if(!phone.getCountry().equalsIgnoreCase(request.getPhone().getCountry())) {
                            phone.setCountry(request.getPhone().getCountry());
                        }
                        if(!phone.getCountryCode().equalsIgnoreCase(request.getPhone().getCountryCode())) {
                            phone.setCountryCode(request.getPhone().getCountryCode());
                        }
                        if(!phone.getIsoCode().equalsIgnoreCase(request.getPhone().getIsoCode())) {
                            phone.setIsoCode(request.getPhone().getIsoCode());
                        }
                        phoneInformationRepository.save(phone);
                    }, () -> {
                        PhoneInformation phone = AccountMapper.INSTANCE.phoneInformation(request.getPhone());
                        phone.setUser(user);
                        phoneInformationRepository.save(phone);
                    });
            user.setUpdatedAt(LocalDateTime.now());
            user.setLastUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public ApiResponse<String> updateAvatar(UpdateProfileRequest request, Profile profile) {
        UploadRequest upload = new UploadRequest();
        upload.setFile(request.getAvatar());

        ApiResponse<String> response = storageService.upload(upload);
        if(response.getStatus().is2xxSuccessful()) {
            profile.setAvatar(response.getData());
            updateTimeStamps(profile.getUser(), profile);
        }
        return response;
    }

    private void updateFirstName(UpdateProfileRequest request, Profile profile) {
        boolean canUpdateFirstName = request.getFirstName() != null
                && !request.getFirstName().isEmpty()
                && !profile.getFirstName().equalsIgnoreCase(request.getFirstName());
        if(canUpdateFirstName) {
            profile.getUser().setFirstName(request.getFirstName());
            profile.setFirstName(request.getFirstName());
            updateTimeStamps(profile.getUser(), profile);
        }
    }

    private void updateLastName(UpdateProfileRequest request, Profile profile) {
        boolean canUpdateLastName = request.getLastName() != null
                && !request.getLastName().isEmpty()
                && !profile.getLastName().equalsIgnoreCase(request.getLastName());
        if(canUpdateLastName) {
            profile.getUser().setLastName(request.getLastName());
            profile.setLastName(request.getLastName());
            updateTimeStamps(profile.getUser(), profile);
        }
    }

    private void updateTimeStamps(User user, Profile profile) {
        user.setUpdatedAt(LocalDateTime.now());
        user.setLastUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);
    }
}
