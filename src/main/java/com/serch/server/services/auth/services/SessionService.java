package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.responses.SessionResponse;

import java.util.UUID;

/**
 * Service interface for managing user sessions.
 *
 * @see com.serch.server.services.auth.services.implementations.SessionImplementation
 */
public interface SessionService {

    /**
     * Revokes all refresh tokens associated with the specified user ID.
     *
     * @param userId The unique identifier of the user.
     */
    void revokeAllRefreshTokens(UUID userId);

    /**
     * Revokes all sessions associated with the specified user ID.
     *
     * @param userId The unique identifier of the user.
     */
    void revokeAllSessions(UUID userId);

    /**
     * Generates a new session based on the provided request.
     *
     * @param request The session request.
     * @return ApiResponse containing the session response.
     *
     * @see RequestSession
     * @see AuthResponse
     * @see ApiResponse
     */
    ApiResponse<AuthResponse> generateSession(RequestSession request);

    /**
     * Refreshes an existing session based on the provided token.
     *
     * @param token The refresh token used to refresh the session.
     * @return ApiResponse containing the refreshed session response.
     *
     * @see ApiResponse
     * @see SessionResponse
     */
    ApiResponse<SessionResponse> refreshSession(String token);

    /**
     * Validates the authenticity and expiration of a session token.
     *
     * @param token The session token to validate.
     * @return ApiResponse indicating the validation status.
     *
     * @see ApiResponse
     */
    ApiResponse<String> validateSession(String token);

    /**
     * Updates the last signed in timestamp of the user.
     */
    void updateLastSignedIn();

    /**
     * Signs out the user by revoking all sessions and refresh tokens.
     */
    void signOut();
}