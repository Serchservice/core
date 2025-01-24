package com.serch.server.domains.referral.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.domains.referral.responses.ReferralProgramResponse;
import com.serch.server.domains.referral.services.implementations.ReferralProgramImplementation;

/**
 * This is the wrapper class for the generation and creation of Referral Program in Serch
 *
 * @see ReferralProgramImplementation
 * @author <a href="https://iamevaristus.github.com">Evaristus Adimonyemma</a>
 */
public interface ReferralProgramService {
    /**
     * Creates a new referral program for the associated user.
     *
     * @param user The referral user to create its program.
     *
     * @see User
     */
    void create(User user);

    /**
     * Verifies a referral link and retrieves the associated ReferralProgram.
     *
     * @param content The referral content to verify.
     * @return An ApiResponse containing the verified referral content.
     *
     * @see ReferralProgramResponse
     * @see ApiResponse
     */
    ApiResponse<ReferralProgramResponse> verifyLink(String content);

    /**
     * Verifies a referral code and retrieves the associated ReferralProgram.
     *
     * @param code The referral code to verify.
     * @return An ApiResponse containing the verified referral link.
     *
     * @see ReferralProgramResponse
     * @see ApiResponse
     */
    ApiResponse<ReferralProgramResponse> verifyCode(String code);

    /**
     * Retrieves the ReferralProgram for the logged-in user.
     *
     * @return An ApiResponse containing the verified referral link.
     *
     * @see ReferralProgramResponse
     * @see ApiResponse
     */
    ApiResponse<ReferralProgramResponse> program();

    /**
     * Verifies a referral code and retrieves the associated user.
     *
     * @param code The referral code to verify.
     * @return The user associated with the referral code.
     *
     * @see User
     */
    User verify(String code);
}