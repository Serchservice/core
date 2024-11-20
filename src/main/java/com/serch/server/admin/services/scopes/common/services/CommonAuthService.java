package com.serch.server.admin.services.scopes.common.services;

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
     * Retrieves the Multi-Factor Authentication (MFA) details for a given user.
     * The response provides information about the available MFA factors associated
     * with the user, such as the type of factor (e.g., SMS, email) and the status of each factor.
     *
     * @param user The {@link User} whose MFA information is being requested.
     *             The user must be authenticated to access their MFA details.
     * @return {@link AccountMFAResponse} containing the details of the user's MFA factors.
     */
    AccountMFAResponse mfa(User user);

    /**
     * Retrieves the list of MFA challenges that a user has made. This includes
     * information about the attempts to authenticate via MFA, such as the date,
     * status, and type of each challenge. Useful for monitoring or troubleshooting
     * MFA-related issues.
     *
     * @param user The {@link User} whose MFA challenges are being requested.
     *             The user should have at least one registered MFA factor to view challenges.
     * @return List of {@link AccountMFAChallengeResponse} representing each MFA challenge.
     */
    List<AccountMFAChallengeResponse> challenges(User user);

    /**
     * Retrieves a list of active and past sessions associated with a given user.
     * Each session entry provides details about the session's start time, last
     * activity, IP address, device information, and whether the session is still active.
     * This data can help with monitoring the user's account activity.
     *
     * @param user The {@link User} whose session history is being requested.
     *             Sessions may include different device types, IPs, and time zones.
     * @return List of {@link AccountSessionResponse} representing the user's sessions.
     */
    List<AccountSessionResponse> sessions(User user);

    /**
     * Provides a summarized view of both the user's MFA factors and session history.
     * This response is intended to give an overview of the user's authentication status,
     * including details such as enabled MFA factors and recent session activity.
     *
     * @param user The {@link User} for whom the authentication overview is being requested.
     *             This is used to quickly assess the security posture of the user's account.
     * @return {@link AccountAuthResponse} summarizing the user's MFA factors and session details.
     */
    AccountAuthResponse auth(User user);

    /**
     * Revokes an active user session identified by the given session ID.
     * This action logs the user out of the associated session, effectively
     * invalidating it and preventing further access. Useful for terminating
     * sessions when suspicious activity is detected.
     *
     * @param sessionId The unique identifier of the session to be revoked.
     *                  It should correspond to an active session record.
     * @return {@link ApiResponse} containing a list of {@link AccountSessionResponse}
     *         reflecting the updated session state after the revocation.
     */
    ApiResponse<List<AccountSessionResponse>> revokeSession(UUID sessionId);

    /**
     * Revokes a refresh token identified by the given refresh token ID.
     * This action prevents the refresh token from being used to obtain new
     * access tokens, effectively logging the user out of any sessions tied
     * to that token. Suitable for handling compromised or expired refresh tokens.
     *
     * @param refreshTokenId The unique identifier of the refresh token to be revoked.
     *                       The token must be associated with an existing session.
     * @return {@link ApiResponse} containing a list of {@link AccountSessionResponse}
     *         showing the affected sessions post-revocation.
     */
    ApiResponse<List<AccountSessionResponse>> revokeRefreshToken(UUID refreshTokenId);
}