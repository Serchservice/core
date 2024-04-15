package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.account.responses.ReferralResponse;

import java.util.List;

/**
 * Service interface for managing referrals within the system.
 *
 * @see com.serch.server.services.account.services.implementations.ReferralImplementation
 * @see com.serch.server.services.account.controllers.ReferralController
 */
public interface ReferralService {

    /**
     * Creates a referral link between two users.
     *
     * @param referral   The user being referred.
     * @param referredBy The user who referred the other user.
     *
     * @see User
     */
    void create(User referral, User referredBy);

    /**
     * Verifies a referral code and retrieves the associated user.
     *
     * @param code The referral code to verify.
     * @return The user associated with the referral code.
     *
     * @see User
     */
    User verifyCode(String code);

    /**
     * Verifies a referral link and retrieves the associated link.
     *
     * @param link The referral link to verify.
     * @return An ApiResponse containing the verified referral link.
     *
     * @see ApiResponse
     */
    ApiResponse<String> verifyLink(String link);

    /**
     * Retrieves referrals made by the currently logged-in user.
     *
     * @return An ApiResponse containing a list of referral responses.
     *
     * @see ReferralResponse
     * @see ApiResponse
     */
    ApiResponse<List<ReferralResponse>> viewReferrals();
}
