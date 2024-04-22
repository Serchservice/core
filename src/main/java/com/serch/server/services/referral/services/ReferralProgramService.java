package com.serch.server.services.referral.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.referral.responses.ReferralProgramResponse;

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
     * @param link The referral link to verify.
     * @return An ApiResponse containing the verified referral link.
     *
     * @see ReferralProgramResponse
     * @see ApiResponse
     */
    ApiResponse<ReferralProgramResponse> verifyLink(String link);

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
     * Verifies a referral code and retrieves the associated user.
     *
     * @param code The referral code to verify.
     * @return The user associated with the referral code.
     *
     * @see User
     */
    User verify(String code);
}