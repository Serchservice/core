package com.serch.server.services.referral.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.others.ReferralException;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.auth.User;
import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.referral.ReferralProgramRepository;
import com.serch.server.repositories.referral.ReferralRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.referral.responses.ReferralProgramResponse;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Generates a referral link based on the user's category.
     * @param category The category of the user (USER, BUSINESS, PROVIDER).
     * @return The generated referral link.
     */
    private String generateReferralLink(Role category) {
        if(category == Role.USER) {
            return "https://serchservice.com/app/join/user?ref=%s".formatted(tokenService.generate(6));
        } else if(category == Role.BUSINESS) {
            return "https://serchservice.com/app/join/business?ref=%s".formatted(tokenService.generate(6));
        } else {
            return "https://serchservice.com/app/join/provider?ref=%s".formatted(tokenService.generate(6));
        }
    }

    /**
     * Extracts the referral code from a referral link.
     * @param referralLink The referral link.
     * @return The extracted referral code.
     */
    private String extractReferralCode(String referralLink) {
        Pattern pattern = Pattern.compile("ref=([a-zA-Z0-9_]+)");
        Matcher matcher = pattern.matcher(referralLink);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new ReferralException("Cannot find referral code from referral link");
        }
    }

    @Override
    public void create(User user) {
        String referralLink = generateReferralLink(user.getRole());

        ReferralProgram program = new ReferralProgram();
        program.setUser(user);
        program.setReferralCode(extractReferralCode(referralLink));
        program.setReferLink(referralLink);
        referralProgramRepository.save(program);
    }

    @Override
    public User verify(String code) {
        return referralProgramRepository.findByReferralCode(code)
                .map(ReferralProgram::getUser)
                .orElseThrow(() -> new ReferralException("Referral not found"));
    }

    @Override
    public ApiResponse<ReferralProgramResponse> verifyLink(String link) {
        ReferralProgram program = referralProgramRepository.findByReferLink(link)
                .orElseThrow(() -> new ReferralException("Referral not found"));

        ReferralProgramResponse response = getResponse(program);
        return new ApiResponse<>("Referral Link found", response, HttpStatus.OK);
    }

    private ReferralProgramResponse getResponse(ReferralProgram program) {
        String firstName = businessProfileRepository.findById(program.getUser().getId())
                .map(BusinessProfile::getBusinessName)
                .orElse(program.getUser().getFirstName());
        String lastName = businessProfileRepository.findById(program.getUser().getId())
                .map(business -> "")
                .orElse(program.getUser().getLastName());

        ReferralProgramResponse response = new ReferralProgramResponse();
        response.setAvatar(referralService.getAvatar(program.getUser()));
        response.setName(String.format("%s %s", firstName, lastName));
        response.setRole(program.getUser().getRole().getType());
        response.setReferralCode(program.getReferralCode());
        response.setReferLink(program.getReferLink());
        return response;
    }

    @Override
    public ApiResponse<ReferralProgramResponse> verifyCode(String code) {
        ReferralProgram program = referralProgramRepository.findByReferralCode(code)
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
