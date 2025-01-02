package com.serch.server.services.referral.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.referral.Referral;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.referral.ReferralRepository;
import com.serch.server.services.referral.responses.ReferralResponse;
import com.serch.server.services.referral.services.ReferralService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service implementation for managing referrals within the system.
 * It implements it wrapper class {@link ReferralService}
 *
 * @see ReferralRepository
 * @see BusinessProfileRepository
 */
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
        refer.setReferredBy(referredBy.getProgram());
        referralRepository.save(refer);
    }

    @Override
    @Transactional
    public void undo(User user) {
        referralRepository.findByReferral_Id(user.getId()).ifPresent(referral -> {
            referralRepository.delete(referral);
            referralRepository.flush();
        });
    }

    @Override
    public ApiResponse<List<ReferralResponse>> viewReferrals(Integer page, Integer size) {
        Page<Referral> referrals = referralRepository.findByReferredBy_User_EmailAddress(UserUtil.getLoginUser(), HelperUtil.getPageable(page, size));
        List<ReferralResponse> list = new ArrayList<>();

        if(referrals != null && !referrals.getContent().isEmpty()) {
            list = referrals.getContent().stream()
                    .sorted(Comparator.comparing(Referral::getCreatedAt))
                    .map(referral -> {
                        ReferralResponse response = new ReferralResponse();

                        String avatar = getAvatar(referral.getReferral());
                        response.setAvatar(avatar);
                        response.setRole(referral.getReferral().getRole().getType());
                        response.setName(referral.getReferral().getFullName());
                        response.setReferId(referral.getReferId());
                        response.setInfo("Joined Serch Platform: " + TimeUtil.formatDay(referral.getReferral().getCreatedAt(), referral.getReferredBy().getUser().getTimezone()));

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
                                .map(BusinessProfile::getBusinessLogo)
                                .orElse("")
                );
    }
}
