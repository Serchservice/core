package com.serch.server.services.verified.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.verified.responses.VerificationResponse;

import java.util.UUID;

public interface VerificationService {
    /**
     * Get the verification details of the logged-in user
     *
     * @return {@link ApiResponse} of {@link VerificationResponse}
     */
    ApiResponse<VerificationResponse> verification();

    /**
     * Get the verification details of the user
     *
     * @param userId The user id being searched for
     *
     * @return {@link VerificationResponse}
     */
    VerificationResponse buildResponse(UUID userId);

    /**
     * Give verification consent
     *
     * @return {@link ApiResponse} of {@link VerificationResponse}
     */
    ApiResponse<VerificationResponse> consent();
}
