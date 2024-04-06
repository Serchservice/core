package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.mappers.AuthMapper;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.services.account.requests.RequestCreateProfile;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.account.services.ReferralService;
import com.serch.server.services.auth.requests.RequestAuth;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileImplementation implements ProfileService {
    private final ProfileRepository profileRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final ReferralService referralService;
    private final WalletService walletService;

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

            walletService.createWallet(profile);
            return new ApiResponse<>("Profile successfully saved", profile, HttpStatus.CREATED);
        }
    }

    @Override
    public ApiResponse<Profile> createProviderProfile(Incomplete incomplete, RequestAuth auth, User user) {
        RequestCreateProfile createProfile = new RequestCreateProfile();
        createProfile.setUser(user);
        createProfile.setProfile(getRequestProfile(incomplete, auth));
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

    private RequestProfile getRequestProfile(Incomplete incomplete, RequestAuth auth) {
        RequestProfile profile = AuthMapper.INSTANCE.profile(incomplete.getProfile());
        profile.setDevice(auth.getDevice());
        profile.setPlatform(auth.getPlatform());
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
}
