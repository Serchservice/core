package com.serch.server.services.account.services.implementations;

import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.account.Referral;
import com.serch.server.repositories.account.ReferralRepository;
import com.serch.server.services.account.services.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferralImplementation implements ReferralService {
    private final ReferralRepository referralRepository;

    @Override
    public void create(Profile profile, Profile referring) {
        Referral referral = new Referral();
        referral.setProfile(profile);
        referral.setReferredBy(referring);
        referralRepository.save(referral);
    }

    @Override
    public void create(BusinessProfile profile, BusinessProfile referring) {
        Referral referral = new Referral();
        referral.setBusiness(profile);
        referral.setBusinessReferredBy(referring);
        referralRepository.save(referral);
    }

    @Override
    public void create(Profile profile, BusinessProfile referring) {
        Referral referral = new Referral();
        referral.setProfile(profile);
        referral.setBusinessReferredBy(referring);
        referralRepository.save(referral);
    }

    @Override
    public void create(BusinessProfile profile, Profile referring) {
        Referral referral = new Referral();
        referral.setBusiness(profile);
        referral.setReferredBy(referring);
        referralRepository.save(referral);
    }
}
