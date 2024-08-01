package com.serch.server.services.referral.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.referral.ReferralController;
import com.serch.server.services.referral.responses.ReferralResponse;

import java.util.List;

/**
 * Service interface for managing referrals within the system.
 *
 * @see ReferralImplementation
 * @see ReferralController
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
     * Removes any saved record of the user
     *
     * @param user The user whose account is to be removed
     */
    void undo(User user);

    /**
     * Retrieves referrals made by the currently logged-in user.
     *
     * @return An ApiResponse containing a list of referral responses.
     *
     * @see ReferralResponse
     * @see ApiResponse
     */
    ApiResponse<List<ReferralResponse>> viewReferrals();

    /**
     * Get user avatar from user
     *
     * @param user  The user to get the avatar of
     *
     * @return String of profile picture
     */
    String getAvatar(User user);
}
