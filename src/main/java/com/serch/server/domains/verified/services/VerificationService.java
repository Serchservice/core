package com.serch.server.domains.verified.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.verified.responses.VerificationResponse;

import java.util.UUID;

public interface VerificationService {

    /**
     * Retrieves the verification details of the currently logged-in user.
     * <p>
     * This method provides information regarding the verification status
     * of the authenticated user. It returns an {@link ApiResponse} that
     * contains a {@link VerificationResponse} object with the verification
     * details.
     * </p>
     *
     * @return An {@link ApiResponse} containing the {@link VerificationResponse}
     *         for the currently logged-in user.
     */
    ApiResponse<VerificationResponse> verification();

    /**
     * Builds the verification response for a specific user.
     * <p>
     * This method fetches the verification details associated with a given user
     * identifier. The details are returned as a {@link VerificationResponse}
     * object, which includes the user's verification status and other related
     * information.
     * </p>
     *
     * @param userId The unique identifier of the user whose verification details
     *               are being requested.
     * @return A {@link VerificationResponse} object containing the verification
     *         details of the specified user.
     */
    VerificationResponse buildResponse(UUID userId);

    /**
     * Provides consent for user verification.
     * <p>
     * This method allows the currently logged-in user to give consent for
     * verification processes. The consent may be required for certain actions
     * or verification steps. The result is an {@link ApiResponse} that contains
     * the updated {@link VerificationResponse} reflecting the user's consent.
     * </p>
     *
     * @return An {@link ApiResponse} containing the {@link VerificationResponse}
     *         after the consent has been given.
     */
    ApiResponse<VerificationResponse> consent();
}