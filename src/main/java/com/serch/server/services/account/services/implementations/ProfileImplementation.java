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
import com.serch.server.services.account.requests.RequestCreateProfile;
import com.serch.server.services.account.requests.UpdateProfileRequest;
import com.serch.server.services.account.responses.MoreProfileData;
import com.serch.server.services.account.responses.ProfileResponse;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.account.services.SpecialtyService;
import com.serch.server.services.auth.requests.RequestPhoneInformation;
import com.serch.server.services.referral.services.ReferralService;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.core.storage.core.StorageService;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
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
    @Transactional
    public ApiResponse<Profile> createProfile(RequestCreateProfile request) {
        var user = profileRepository.findById(request.getUser().getId());
        if(user.isPresent()) {
            throw new AccountException("User already have a profile");
        } else {
            Profile profile = getProfile(request);
            PhoneInformation phoneInformation = AccountMapper.INSTANCE.phoneInformation(
                    request.getProfile().getPhoneInformation()
            );
            phoneInformation.setUser(request.getUser());
            phoneInformationRepository.save(phoneInformation);

            if(request.getReferredBy() != null) {
                referralService.create(request.getUser(), request.getReferredBy());
            }

            walletService.create(profile.getUser());
            return new ApiResponse<>("Profile successfully saved", profile, HttpStatus.CREATED);
        }
    }

    private Profile getProfile(RequestCreateProfile request) {
        Profile profile = AccountMapper.INSTANCE.profile(request.getProfile());
        profile.setUser(request.getUser());
        profile.setCategory(request.getCategory());
        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public ApiResponse<Profile> createProviderProfile(Incomplete incomplete, User user) {
        RequestCreateProfile createProfile = new RequestCreateProfile();
        createProfile.setUser(user);
        createProfile.setProfile(getRequestProfile(incomplete));
        createProfile.setCategory(incomplete.getCategory().getCategory());

        if(incomplete.getReferredBy() != null) {
            createProfile.setReferredBy(incomplete.getReferredBy().getReferredBy());
        }
        return createProfile(createProfile);
    }

    private RequestProfile getRequestProfile(Incomplete incomplete) {
        RequestProfile profile = AuthMapper.INSTANCE.profile(incomplete.getProfile());
        profile.setPassword(incomplete.getProfile().getPassword());
        profile.setEmailAddress(incomplete.getEmailAddress());
        profile.setPhoneInformation(AuthMapper.INSTANCE.phoneInformation(incomplete.getPhoneInfo()));
        return profile;
    }

    @Override
    @Transactional
    public ApiResponse<Profile> createUserProfile(RequestProfile request, User user, User referral) {
        RequestCreateProfile createProfile = new RequestCreateProfile();
        createProfile.setUser(user);
        createProfile.setProfile(request);
        createProfile.setCategory(SerchCategory.USER);
        createProfile.setReferredBy(referral);
        return createProfile(createProfile);
    }

    @Override
    @Transactional
    public ApiResponse<ProfileResponse> profile() {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new AccountException("Profile not found"));

        return new ApiResponse<>(profile(profile));
    }

    @Override
    @Transactional
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

        PhoneInformation phoneInformation = phoneInformationRepository.findByUser_Id(profile.getId())
                .orElse(new PhoneInformation());
        response.setPhoneInfo(AccountMapper.INSTANCE.phoneInformation(phoneInformation));
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

    @Override
    @Transactional
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
        more.setLastSignedIn(TimeUtil.formatLastSignedIn(user.getLastSignedIn(), false));
        return more;
    }

    @Override
    @Transactional
    public void undo(String emailAddress) {
        profileRepository.findByUser_EmailAddress(emailAddress)
                .ifPresent(profile -> {
                    userRepository.delete(profile.getUser());
                    phoneInformationRepository.findByUser_Id(profile.getId()).ifPresent(phoneInformationRepository::delete);
                    referralService.undo(profile.getUser());
                    walletService.undo(profile.getUser());
                    profileRepository.delete(profile);
                });
    }

    @Override
    @Transactional
    public ApiResponse<ProfileResponse> update(UpdateProfileRequest request) {
        User user = userUtil.getUser();
        Profile profile = profileRepository.findById(user.getId())
                .orElseThrow(() -> new AccountException("Profile not found"));
        if(user.isProfile()) {
            if(user.getProfileLastUpdatedAt() == null) {
                return getProfileUpdateResponse(request, user, profile);
            } else {
                Duration duration = Duration.between(user.getProfileLastUpdatedAt(), LocalDateTime.now());
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

    @Transactional
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
    @Transactional
    public void updatePhoneInformation(RequestPhoneInformation request, User user) {
        if(request != null) {
            phoneInformationRepository.findByUser_Id(user.getId())
                    .ifPresentOrElse(phone -> {
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
                        PhoneInformation phone = AccountMapper.INSTANCE.phoneInformation(request);
                        phone.setUser(user);
                        phoneInformationRepository.save(phone);
                    });
            user.setUpdatedAt(LocalDateTime.now());
            user.setLastUpdatedAt(LocalDateTime.now());
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
        boolean canUpdateGender = request.getGender() != null
                && profile.getGender() != request.getGender();
        if(canUpdateGender) {
            profile.setGender(request.getGender());
            profile.setUpdatedAt(LocalDateTime.now());
            profileRepository.save(profile);
        }
    }

    private void updateTimeStamps(User user, Profile profile) {
        user.setProfileLastUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);
    }
}