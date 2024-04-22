package com.serch.server.services.referral.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.account.ReferralException;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.repositories.referral.ReferralProgramRepository;
import com.serch.server.services.referral.responses.ReferralProgramResponse;
import com.serch.server.services.referral.services.ReferralProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ReferralProgramImplementation implements ReferralProgramService {
    private final ReferralProgramRepository referralProgramRepository;

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
    };

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
            throw new SerchException("Cannot find referral code from referral link");
        }
    }

    @Override
    public void create(User user) {
        String referralLink = generateReferralLink(user.getFirstName(), user.getLastName(), user.getRole());
        String referralCode = extractReferralCode(referralLink);

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
        return new ApiResponse<>("Link found", HttpStatus.OK);
    }

    @Override
    public ApiResponse<ReferralProgramResponse> verifyCode(String code) {
        return null;
    }
}
