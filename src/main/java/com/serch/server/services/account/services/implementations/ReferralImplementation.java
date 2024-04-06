package com.serch.server.services.account.services.implementations;

import com.serch.server.exceptions.account.ReferralException;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.account.Referral;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.account.ReferralRepository;
import com.serch.server.services.account.services.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferralImplementation implements ReferralService {
    private final ReferralRepository referralRepository;
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;

    @Override
    public void create(User referral, User referredBy) {
        Referral refer = new Referral();
        refer.setReferral(referral);
        refer.setReferredBy(referredBy);
        referralRepository.save(refer);
    }

    @Override
    public User verifyReferralCode(String code) {
        return profileRepository.findByReferralCodeIgnoreCase(code)
                .map(Profile::getUser)
                .orElse(
                        businessProfileRepository.findByReferralCodeIgnoreCase(code)
                                .map(BusinessProfile::getUser)
                                .orElseThrow(() -> new ReferralException("Referral not found"))
                );
    }
}
