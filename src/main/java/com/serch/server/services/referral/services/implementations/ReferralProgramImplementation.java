package com.serch.server.services.referral.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.referral.ReferralReward;
import com.serch.server.exceptions.others.ReferralException;
import com.serch.server.mappers.ReferralMapper;
import com.serch.server.models.auth.User;
import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.repositories.referral.ReferralProgramRepository;
import com.serch.server.repositories.referral.ReferralRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.referral.responses.ReferralProgramData;
import com.serch.server.services.referral.responses.ReferralProgramResponse;
import com.serch.server.services.referral.services.ReferralProgramService;
import com.serch.server.services.referral.services.ReferralService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the class the implements the wrapper class for the implementation of {@link ReferralProgramService}
 *
 * @see ReferralService
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
    private final ReferralProgramRepository referralProgramRepository;
    private final ReferralRepository referralRepository;
    private final SharedLinkRepository sharedLinkRepository;
    private final TripRepository tripRepository;

    /**
     * Generates a referral code based on the user's first name, last name, and category.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param category The category of the user (USER, BUSINESS, PROVIDER).
     * @return The generated referral code.
     */
    private String code(String firstName, String lastName, Role category) {
        if(category == Role.BUSINESS) {
            return "%sBusiness%s".formatted(firstName, UUID.randomUUID().toString().substring(0, 4)
                    .replaceAll("-", "").replaceAll("_", "")
            );
        } else {
            return (firstName.substring(0, 3) + lastName.substring(0, 3) +
                    UUID.randomUUID().toString().substring(0, 4))
                    .replaceAll("-", "")
                    .replaceAll("_", "");
        }
    }

    /**
     * Generates a referral link based on the user's first name, last name, and category.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param category The category of the user (USER, BUSINESS, PROVIDER).
     * @return The generated referral link.
     */
    private String generateReferralLink(String firstName, String lastName, Role category) {
        if(category == Role.USER) {
            return "https://serchservice.com/join_the_serch_user_app?ref=%s".formatted(
                    code(firstName, lastName, category)
            );
        } else if(category == Role.BUSINESS) {
            return "https://serchservice.com/join_the_serch_business_app?ref=%s".formatted(
                    code(firstName, lastName, category)
            );
        } else {
            return "https://serchservice.com/join_the_serch_provider_app?ref=%s".formatted(
                    code(firstName, lastName, category)
            );
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

    private ReferralReward getReward() {
        SecureRandom random = new SecureRandom();
        ReferralReward[] rewards = ReferralReward.values();
        int randomIndex = random.nextInt(rewards.length);
        return rewards[randomIndex];
    }

    @Override
    public void create(User user) {
        String referralLink = generateReferralLink(user.getFirstName(), user.getLastName(), user.getRole());
        ReferralReward reward = getReward();

        ReferralProgram program = new ReferralProgram();
        program.setUser(user);
        program.setReferralCode(extractReferralCode(referralLink));
        program.setReferLink(referralLink);
        program.setReward(reward);

        if(reward == ReferralReward.REFER_TIERED || reward == ReferralReward.ME_TIERED) {
            program.setMilestone(5);
        }
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
        ReferralProgramResponse response = new ReferralProgramResponse();
        response.setAvatar(referralService.getAvatar(program.getUser()));
        response.setName(program.getUser().getFullName());
        response.setRole(program.getUser().getRole().getType());

        ReferralProgramData data = ReferralMapper.INSTANCE.data(program);
        data.setCredit(program.getReward().getCredit());
        data.setDescription(program.getReward().getDescription());
        response.setData(data);
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

    @Override
    public void performChecks() {
        referralProgramRepository.findAll()
                .forEach(program -> {
                    int size;
                    switch (program.getReward()) {
                        case REFER_TIERED:
                            size = referralRepository.countReferralsOfReferralsByReferredBy(program);
                            if (size == program.getMilestone()) {
                                updateProgramCredits(program, 5);
                            }
                            break;
                        case SHARE_LOYALTY:
                            size = sharedLinkRepository.countSharedLinksOfReferralsOfReferralsByReferredBy(program);
                            if (size > program.getMilestone()) {
                                updateProgramCredits(program, size);
                            }
                            break;
                        case ME_SHARE_LOYALTY:
                            size = sharedLinkRepository.countSharedLinksForUser(program.getUser().getId());
                            if (size > program.getMilestone()) {
                                updateProgramCredits(program, size);
                            }
                            break;
                        case ME_TRIP_MILESTONE:
                            size = tripRepository.countCompletedTripsForUserAndAccount(String.valueOf(program.getUser().getId()));
                            if (size > program.getMilestone()) {
                                updateProgramCredits(program, size);
                            }
                            break;
                        case TIME_LIMITED:
                            LocalDateTime now = LocalDateTime.now();
                            LocalDateTime startDate = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                            LocalDateTime endDate = now.withDayOfMonth(15).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                            size = referralRepository.countTimeLimitedReferralsOfReferralsByReferredBy(program, startDate, endDate);
                            if (size > 0) {
                                updateProgramCredits(program, 0); // Not updating milestone for time-limited rewards
                            }
                            break;
                        case ME_TIME_LIMITED:
                            LocalDateTime today = LocalDateTime.now();
                            LocalDateTime start = today.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                            LocalDateTime end = today.withDayOfMonth(15).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                            size = referralRepository.countTimeLimitedReferralsByReferredBy(program, start, end);
                            if (size > 0) {
                                updateProgramCredits(program, 0); // Not updating milestone for time-limited rewards
                            }
                            break;
                        case TRIP_MILESTONE:
                            size = tripRepository.countCompletedTripsOfReferralsOfReferralsByReferredByAndAccount(program.getUser().getId());
                            if (size > program.getMilestone()) {
                                updateProgramCredits(program, size);
                            }
                            break;
                        default:
                            size = referralRepository.findByReferredBy_User_EmailAddress(program.getUser().getEmailAddress()).size();
                            if (size == program.getMilestone()) {
                                updateProgramCredits(program, 5);
                            }
                            break;
                    }
                });
    }

    private void updateProgramCredits(ReferralProgram program, int milestoneIncrement) {
        program.setCredits(program.getCredits().add(BigDecimal.valueOf(program.getReward().getCredit())));
        program.setUpdatedAt(LocalDateTime.now());
        program.setMilestone(program.getMilestone() + milestoneIncrement);
        referralProgramRepository.save(program);
    }
}
