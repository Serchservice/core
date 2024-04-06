package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.services.account.services.BusinessProfileService;
import com.serch.server.services.account.services.ReferralService;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessProfileImplementation implements BusinessProfileService {
    private final TokenService tokenService;
    private final ReferralService referralService;
    private final WalletService walletService;
    private final BusinessProfileRepository businessProfileRepository;
    private final PhoneInformationRepository phoneInformationRepository;

    @Override
    public ApiResponse<String> createProfile(Incomplete incomplete, User user) {
        BusinessProfile businessProfile = saveBusinessProfile(incomplete, user);
        savePhoneInformation(incomplete, user);
        if(incomplete.getReferredBy() != null) {
            referralService.create(user, incomplete.getReferredBy().getReferredBy());
        }
        walletService.createWallet(businessProfile);
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
        String referLink = HelperUtil.generateReferralLink(
                incomplete.getProfile().getFirstName(),
                incomplete.getProfile().getBusinessName(),
                SerchCategory.BUSINESS
        );
        String defaultPassword = "@%s%s".formatted(
                incomplete.getProfile().getBusinessName().toUpperCase(),
                tokenService.generateCode(2)
        );

        BusinessProfile businessProfile = AccountMapper.INSTANCE.profile(incomplete.getProfile());
        businessProfile.setUser(user);
        businessProfile.setReferLink(referLink);
        businessProfile.setEmailAddress(user.getEmailAddress());
        businessProfile.setCategory(incomplete.getCategory().getCategory());
        businessProfile.setDefaultPassword(defaultPassword);
        businessProfile.setReferralCode(HelperUtil.extractReferralCode(referLink));
        return businessProfileRepository.save(businessProfile);
    }
}
