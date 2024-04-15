package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.account.ReferralException;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.account.Referral;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.account.ReferralRepository;
import com.serch.server.services.account.responses.ReferralResponse;
import com.serch.server.services.account.services.ReferralService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service implementation for managing referrals within the system.
 * It implements it wrapper class {@link ReferralService}
 *
 * @see ReferralRepository
 * @see ProfileRepository
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
        refer.setReferredBy(referredBy);
        referralRepository.save(refer);
    }

    @Override
    public User verifyCode(String code) {
        return profileRepository.findByReferralCodeIgnoreCase(code)
                .map(Profile::getUser)
                .orElse(
                        businessProfileRepository.findByReferralCodeIgnoreCase(code)
                                .map(BusinessProfile::getUser)
                                .orElseThrow(() -> new ReferralException("Referral not found"))
                );
    }

    @Override
    public ApiResponse<String> verifyLink(String link) {
        String referLink = profileRepository.findByReferLink(link)
                .map(Profile::getReferLink)
                .orElse(
                        businessProfileRepository.findByReferLink(link)
                                .map(BusinessProfile::getReferLink)
                                .orElseThrow(() -> new ReferralException("Referral not found"))
                );
        return new ApiResponse<>("Link found", referLink, HttpStatus.OK);
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
