package com.serch.server.admin.services.scopes.common;

import com.serch.server.admin.services.responses.auth.AccountAuthResponse;
import com.serch.server.admin.services.responses.auth.AccountMFAResponse;
import com.serch.server.admin.services.responses.auth.AccountMFAChallengeResponse;
import com.serch.server.admin.services.responses.auth.AccountSessionResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;

import java.util.List;
import java.util.UUID;

public interface CommonAuthService {
    /**
     * Get the {@link com.serch.server.models.auth.mfa.MFAFactor} response for the user
     *
     * @param user The {@link User} whose data is being requested for
     *
     * @return {@link AccountMFAResponse}
     */
    AccountMFAResponse mfa(User user);

    /**
     * Get the list of {@link com.serch.server.models.auth.mfa.MFAChallenge} made by the user
     *
     * @param user The {@link User} whose data is being requested for
     *
     * @return List of {@link AccountMFAChallengeResponse}
     */
    List<AccountMFAChallengeResponse> challenges(User user);

    /**
     * Get the list of {@link com.serch.server.models.auth.Session} made by the user
     *
     * @param user The {@link User} whose data is being requested for
     *
     * @return List of {@link AccountSessionResponse}
     */
    List<AccountSessionResponse> sessions(User user);

    /**
     * Get summarized version of {@link com.serch.server.models.auth.mfa.MFAFactor}
     * and {@link com.serch.server.models.auth.Session} to have an overview
     *
     * @param user The {@link User} whose data is being requested for
     *
     * @return {@link AccountAuthResponse}
     */
    AccountAuthResponse auth(User user);

    /**
     * Revoke an active session
     *
     * @param sessionId The session id to be revoked
     *
     * @return {@link ApiResponse} list of {@link AccountSessionResponse}
     */
    ApiResponse<List<AccountSessionResponse>> revokeSession(UUID sessionId);

    /**
     * Revoke an active refresh token
     *
     * @param refreshTokenId The refresh token id to be revoked
     *
     * @return {@link ApiResponse} list of {@link AccountSessionResponse}
     */
    ApiResponse<List<AccountSessionResponse>> revokeRefreshToken(UUID refreshTokenId);
}