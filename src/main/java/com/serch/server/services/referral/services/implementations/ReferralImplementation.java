package com.serch.server.services.referral.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.business.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.referral.Referral;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.business.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.referral.ReferralRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.services.referral.responses.ReferralData;
import com.serch.server.services.referral.responses.ReferralResponse;
import com.serch.server.services.referral.services.ReferralService;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service implementation for managing referrals within the system.
 * It implements it wrapper class {@link ReferralService}
 *
 * @see ReferralRepository
 * @see BusinessProfileRepository
 * @see SharedLinkRepository
 */
@Service
@RequiredArgsConstructor
public class ReferralImplementation implements ReferralService {
    private final ReferralRepository referralRepository;
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final SharedLinkRepository sharedLinkRepository;

    @Override
    public void create(User referral, User referredBy) {
        Referral refer = new Referral();
        refer.setReferral(referral);
        refer.setReferredBy(referredBy.getProgram());
        referralRepository.save(refer);
    }

    @Override
    public void undo(User user) {
        referralRepository.findByReferral_Id(user.getId()).ifPresent(referralRepository::delete);
    }

    private String getCount(User user, User referredBy) {
        if(referredBy.getProgram().isReferral()) {
            return "Total Referrals: " + referralRepository.findByReferredBy_User_EmailAddress(user.getEmailAddress()).size();
        } else if(referredBy.getProgram().isSharing()) {
            return "Total Shared: " + sharedLinkRepository.findByUserId(user.getId()).size();
        } else {
            return "No referral program";
        }
    }

    @Override
    public ApiResponse<List<ReferralResponse>> viewReferrals() {
        List<Referral> referrals = referralRepository.findByReferredBy_User_EmailAddress(UserUtil.getLoginUser());
        List<ReferralResponse> list = new ArrayList<>();

        if(!referrals.isEmpty()) {
            list = referrals.stream()
                    .sorted(Comparator.comparing(Referral::getCreatedAt))
                    .map(referral -> {
                        ReferralResponse response = new ReferralResponse();

                        String avatar = getAvatar(referral.getReferral());
                        response.setAvatar(avatar);
                        response.setRole(referral.getReferral().getRole().getType());
                        response.setName(referral.getReferral().getFullName());
                        response.setReferId(referral.getReferId());

                        ReferralData data = new ReferralData();
                        data.setInfo(getCount(referral.getReferral(), referral.getReferredBy().getUser()));
                        data.setLabel("Joined Serch Platform: " + TimeUtil.formatDay(referral.getReferral().getCreatedAt()));
                        response.setData(data);
                        return response;
                    })
                    .toList();
        }
        return new ApiResponse<>(list);
    }

    @Override
    public String getAvatar(User user) {
        return profileRepository.findById(user.getId())
                .map(Profile::getAvatar)
                .orElse(
                        businessProfileRepository.findById(user.getId())
                                .map(BusinessProfile::getAvatar)
                                .orElse("")
                );
    }
}
