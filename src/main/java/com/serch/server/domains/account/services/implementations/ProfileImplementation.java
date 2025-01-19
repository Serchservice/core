package com.serch.server.domains.account.services.implementations;

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
import com.serch.server.models.certificate.Certificate;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.certificate.CertificateRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.domains.account.requests.RequestCreateProfile;
import com.serch.server.domains.account.requests.UpdateProfileRequest;
import com.serch.server.domains.account.responses.MoreProfileData;
import com.serch.server.domains.account.responses.ProfileResponse;
import com.serch.server.domains.account.services.ProfileService;
import com.serch.server.domains.account.services.SpecialtyService;
import com.serch.server.domains.auth.requests.RequestPhoneInformation;
import com.serch.server.domains.referral.services.ReferralService;
import com.serch.server.domains.auth.requests.RequestProfile;
import com.serch.server.core.storage.services.StorageService;
import com.serch.server.domains.transaction.services.WalletService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

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
    private final StorageService supabase;
    private final ReferralService referralService;
    private final WalletService walletService;
    private final SpecialtyService specialtyService;
    private final UserUtil userUtil;
    private final ProfileRepository profileRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final ShopRepository shopRepository;
    private final RatingRepository ratingRepository;
    private final SharedLinkRepository sharedLinkRepository;
    private final TripRepository tripRepository;
    private final CertificateRepository certificateRepository;

    @Value("${application.account.duration}")
    private Integer ACCOUNT_DURATION;

    @Override
    public ApiResponse<Profile> createProfile(RequestCreateProfile request) {
        var user = profileRepository.findById(request.getUser().getId());

        if(user.isPresent()) {
            throw new AccountException("User already have a profile");
        } else {
            Profile profile = getProfile(request);
            savePhoneInformation(request.getProfile().getPhoneInformation(), request.getUser());

            if(request.getReferredBy() != null) {
                referralService.create(request.getUser(), request.getReferredBy());
            }

            walletService.create(profile.getUser());
            return new ApiResponse<>("Profile successfully saved", profile, HttpStatus.CREATED);
        }
    }

    private void savePhoneInformation(RequestPhoneInformation request, User user) {
        PhoneInformation phoneInformation = AccountMapper.INSTANCE.phoneInformation(request);

        phoneInformation.setUser(user);
        phoneInformationRepository.save(phoneInformation);
    }

    private Profile getProfile(RequestCreateProfile request) {
        Profile profile = AccountMapper.INSTANCE.profile(request.getProfile());
        profile.setUser(request.getUser());
        profile.setCategory(request.getCategory());

        return profileRepository.save(profile);
    }

    @Override
    public ApiResponse<Profile> createProviderProfile(Incomplete incomplete, User user) {
        RequestCreateProfile profile = new RequestCreateProfile();
        profile.setUser(user);
        profile.setProfile(getRequestProfile(incomplete));
        profile.setCategory(incomplete.getCategory().getCategory());

        if(incomplete.getReferredBy() != null) {
            profile.setReferredBy(incomplete.getReferredBy().getReferredBy());
        }

        return createProfile(profile);
    }

    private RequestProfile getRequestProfile(Incomplete incomplete) {
        RequestProfile profile = AuthMapper.INSTANCE.profile(incomplete.getProfile());
        profile.setPassword(incomplete.getProfile().getPassword());
        profile.setEmailAddress(incomplete.getEmailAddress());
        profile.setPhoneInformation(AuthMapper.INSTANCE.phoneInformation(incomplete.getPhoneInfo()));

        return profile;
    }

    @Override
    public ApiResponse<Profile> createUserProfile(RequestProfile request, User user, User referral) {
        RequestCreateProfile profile = new RequestCreateProfile();
        profile.setUser(user);
        profile.setProfile(request);
        profile.setCategory(SerchCategory.USER);
        profile.setReferredBy(referral);

        return createProfile(profile);
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
        response.setCertificate(certificateRepository.findByUser(profile.getId()).map(Certificate::getDocument).orElse(""));
        response.setStatus("");
        response.setFirstName(profile.getUser().getFirstName());
        response.setLastName(profile.getUser().getLastName());
        response.setEmailAddress(profile.getUser().getEmailAddress());
        response.setVerificationStatus(VerificationStatus.NOT_VERIFIED);

        if(profile.isAssociate()) {
            response.setBusinessInformation(AccountMapper.INSTANCE.business(profile.getBusiness()));
        }

        addPhoneInformation(profile, response);
        response.setMore(moreInformation(profile.getUser()));

        response.setSpecializations(
                specialtyRepository.findByProfile_Id(profile.getId()).isEmpty() ? List.of() : specialtyRepository.findByProfile_Id(profile.getId())
                        .stream()
                        .map(specialtyService::response)
                        .toList()
        );
        response.setCategory(profile.getCategory().getType());
        response.setImage(profile.getCategory().getImage());

        return response;
    }

    private void addPhoneInformation(Profile profile, ProfileResponse response) {
        PhoneInformation phoneInformation = phoneInformationRepository.findByUser_Id(profile.getId())
                .orElse(new PhoneInformation());
        response.setPhoneInfo(AccountMapper.INSTANCE.phoneInformation(phoneInformation));
    }

    @Override
    public MoreProfileData moreInformation(User user) {
        MoreProfileData more = new MoreProfileData();
        more.setNumberOfRating(ratingRepository.findByRated(String.valueOf(user.getId())).size());
        more.setTotalShared(sharedLinkRepository.findByUserId(user.getId()).size());
        more.setNumberOfShops(shopRepository.findByUser_Id(user.getId()).size());

        if(user.isProvider()) {
            more.setTotalServiceTrips(tripRepository.findByProviderId(user.getId()).size());
        } else {
            more.setTotalServiceTrips(tripRepository.findByAccount(String.valueOf(user.getId())).size());
        }
        more.setLastSignedIn(TimeUtil.formatLastSignedIn(user.getLastSignedIn(), user.getTimezone(), false));

        return more;
    }

    @Override
    public ApiResponse<ProfileResponse> update(UpdateProfileRequest request) {
        User user = userUtil.getUser();
        Profile profile = profileRepository.findById(user.getId())
                .orElseThrow(() -> new AccountException("Profile not found"));
        if(user.isProfile()) {
            if(user.getProfileLastUpdatedAt() == null) {
                return getProfileUpdateResponse(request, user, profile);
            } else {
                Duration duration = Duration.between(user.getProfileLastUpdatedAt(), TimeUtil.now());
                long remaining = ACCOUNT_DURATION - duration.toDays();

                if(remaining < 0) {
                    return getProfileUpdateResponse(request, user, profile);
                } else {
                    throw new AccountException("You can update your profile in the next %s days".formatted(remaining));
                }
            }
        } else {
            throw new AccountException("Access denied. Cannot perform action");
        }
    }

    protected ApiResponse<ProfileResponse> getProfileUpdateResponse(UpdateProfileRequest request, User user, Profile profile) {
        updateLastName(request, profile);
        updateFirstName(request, profile);
        updatePhoneInformation(request.getPhone(), user);
        updateGender(request, profile);

        if(!HelperUtil.isUploadEmpty(request.getUpload())) {
            String url = supabase.upload(request.getUpload(), UserUtil.getBucket(user.getRole()));
            profile.setAvatar(url);
            updateTimeStamps(profile.getUser(), profile);
        }

        return profile();
    }

    @Override
    public void updatePhoneInformation(RequestPhoneInformation request, User user) {
        if(request != null) {
            phoneInformationRepository.findByUser_Id(user.getId()).ifPresentOrElse(phone -> {
                if(!phone.getPhoneNumber().equalsIgnoreCase(request.getPhoneNumber())) {
                    phone.setPhoneNumber(request.getPhoneNumber());
                }
                if(!phone.getCountry().equalsIgnoreCase(request.getCountry())) {
                    phone.setCountry(request.getCountry());
                }
                if(!phone.getCountryCode().equalsIgnoreCase(request.getCountryCode())) {
                    phone.setCountryCode(request.getCountryCode());
                }
                if(!phone.getIsoCode().equalsIgnoreCase(request.getIsoCode())) {
                    phone.setIsoCode(request.getIsoCode());
                }
                phoneInformationRepository.save(phone);
            }, () -> {
                savePhoneInformation(request, user);
            });
            user.setUpdatedAt(TimeUtil.now());
            user.setLastUpdatedAt(TimeUtil.now());
            userRepository.save(user);
        }
    }

    private void updateFirstName(UpdateProfileRequest request, Profile profile) {
        boolean canUpdateFirstName = request.getFirstName() != null
                && !request.getFirstName().isEmpty()
                && !profile.getUser().getFirstName().equalsIgnoreCase(request.getFirstName());

        if(canUpdateFirstName) {
            profile.getUser().setFirstName(request.getFirstName());
            updateTimeStamps(profile.getUser(), profile);
        }
    }

    private void updateLastName(UpdateProfileRequest request, Profile profile) {
        boolean canUpdateLastName = request.getLastName() != null
                && !request.getLastName().isEmpty()
                && !profile.getUser().getLastName().equalsIgnoreCase(request.getLastName());

        if(canUpdateLastName) {
            profile.getUser().setLastName(request.getLastName());
            updateTimeStamps(profile.getUser(), profile);
        }
    }

    private void updateGender(UpdateProfileRequest request, Profile profile) {
        if(request.getGender() != null && profile.getGender() != request.getGender()) {
            profile.setGender(request.getGender());
            profile.setUpdatedAt(TimeUtil.now());
            profileRepository.save(profile);
        }
    }

    private void updateTimeStamps(User user, Profile profile) {
        user.setProfileLastUpdatedAt(TimeUtil.now());
        userRepository.save(user);

        profile.setUpdatedAt(TimeUtil.now());
        profileRepository.save(profile);
    }
}