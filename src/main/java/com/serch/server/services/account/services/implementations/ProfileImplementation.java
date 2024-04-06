package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.account.Profile;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.services.account.requests.RequestCreateProfile;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.account.services.ReferralService;
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
    public ApiResponse<String> createProfile(RequestCreateProfile request) {
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
            phoneInformation.setProfile(profile);
            phoneInformationRepository.save(phoneInformation);

            if(request.getReferredBy() != null) {
                referralService.create(request.getUser(), request.getReferredBy());
            }

            walletService.createWallet(profile);
            return new ApiResponse<>("Profile successfully saved", HttpStatus.CREATED);
        }
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
