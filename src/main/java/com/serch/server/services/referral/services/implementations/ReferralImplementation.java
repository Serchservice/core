package com.serch.server.services.referral.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.referral.Referral;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.referral.ReferralRepository;
import com.serch.server.repositories.referral.ReferralProgramRepository;
import com.serch.server.services.referral.responses.ReferralResponse;
import com.serch.server.services.referral.services.ReferralService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service implementation for managing referrals within the system.
 * It implements it wrapper class {@link ReferralService}
 *
 * @see ReferralRepository
 */
@Service
@RequiredArgsConstructor
public class ReferralImplementation implements ReferralService {
    private final ReferralRepository referralRepository;
    private final ReferralProgramRepository referralProgramRepository;

    @Override
    public void create(User referral, User referredBy) {
        Referral refer = new Referral();
        refer.setReferral(referral);
        refer.setReferredBy(referredBy.getProgram());
        referralRepository.save(refer);
    }

    @Override
    public ApiResponse<List<ReferralResponse>> viewReferrals() {
        List<ReferralResponse> list = referralRepository.findByReferredBy_EmailAddress(UserUtil.getLoginUser())
                .stream()
                .sorted(Comparator.comparing(Referral::getCreatedAt))
                .map(referral -> {
                    ReferralResponse response = new ReferralResponse();

                    String avatar = profileRepository.findById(referral.getReferral().getId())
                            .map(Profile::getAvatar)
                            .orElse(
                                    businessProfileRepository.findById(referral.getReferral().getId())
                                            .map(BusinessProfile::getAvatar)
                                            .orElse("")
                            );
                    response.setAvatar(avatar);
                    response.setRole(referral.getReferral().getRole().getType());
                    response.setName(referral.getReferral().getFullName());
                    response.setStatus(referral.getStatus());
                    response.setId(referral.getReferId());
                    return response;
                })
                .toList();
        return new ApiResponse<>(list);
    }
}
