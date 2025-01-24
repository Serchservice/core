package com.serch.server.domains.referral.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.token.TokenService;
import com.serch.server.domains.referral.responses.ReferralProgramResponse;
import com.serch.server.domains.referral.services.ReferralProgramService;
import com.serch.server.domains.referral.services.ReferralService;
import com.serch.server.exceptions.others.ReferralException;
import com.serch.server.mappers.ReferralMapper;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.auth.User;
import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.referral.ReferralProgramRepository;
import com.serch.server.repositories.referral.ReferralRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.utils.LinkUtils;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * This is the class the implements the wrapper class for the implementation of {@link ReferralProgramService}
 *
 * @see ReferralService
 * @see TokenService
 * @see ReferralProgramRepository
 * @see ReferralRepository
 * @see SharedLinkRepository
 * @see TripRepository
 * @see UserUtil
 *
 * @author <a href="https://iamevaristus.github.com">Evaristus Adimonyemma</a>
 */
@Service
@RequiredArgsConstructor
public class ReferralProgramImplementation implements ReferralProgramService {
    private final ReferralService referralService;
    private final TokenService tokenService;
    private final ReferralProgramRepository referralProgramRepository;
    private final BusinessProfileRepository businessProfileRepository;

    @Override
    public void create(User user) {
        ReferralProgram program = new ReferralProgram();
        program.setUser(user);
        program.setReferralCode(tokenService.generate(user.getFullName(), 6));
        referralProgramRepository.save(program);
    }

    @Override
    public User verify(String code) {
        return referralProgramRepository.findByReferralCode(code)
                .map(ReferralProgram::getUser)
                .orElseThrow(() -> new ReferralException("Referral not found"));
    }

    @Override
    public ApiResponse<ReferralProgramResponse> verifyLink(String content) {
        ReferralProgram program = referralProgramRepository.findByReferralCode(LinkUtils.instance.referral(content))
                .orElseThrow(() -> new ReferralException("Referral not found"));

        ReferralProgramResponse response = getResponse(program);
        return new ApiResponse<>("Referral Link found", response, HttpStatus.OK);
    }

    private ReferralProgramResponse getResponse(ReferralProgram program) {
        ReferralProgramResponse response = ReferralMapper.instance.response(program);
        response.setAvatar(referralService.getAvatar(program.getUser()));
        response.setReferLink(LinkUtils.instance.referral(program.getUser().getRole(), program.getReferralCode()));
        response.setName(String.format("%s %s", getFirstName(program), getLastName(program)));

        return response;
    }

    private String getLastName(ReferralProgram program) {
        return businessProfileRepository.findById(program.getUser().getId())
                .map(business -> "")
                .orElse(program.getUser().getLastName());
    }

    private String getFirstName(ReferralProgram program) {
        return businessProfileRepository.findById(program.getUser().getId())
                .map(BusinessProfile::getBusinessName)
                .orElse(program.getUser().getFirstName());
    }

    @Override
    public ApiResponse<ReferralProgramResponse> verifyCode(String code) {
        ReferralProgram program = referralProgramRepository.findByReferralCode(LinkUtils.instance.referral(code))
                .orElseThrow(() -> new ReferralException("Referral not found"));

        ReferralProgramResponse response = getResponse(program);
        return new ApiResponse<>("Referral Code found", response, HttpStatus.OK);
    }

    @Override
    public ApiResponse<ReferralProgramResponse> program() {
        ReferralProgram program = referralProgramRepository.findByUser_EmailAddress(UserUtil.getLoginUser())
                .orElseThrow(() -> new ReferralException("Referral not found"));

        ReferralProgramResponse response = getResponse(program);
        return new ApiResponse<>("Referral fetched", response, HttpStatus.OK);
    }
}
