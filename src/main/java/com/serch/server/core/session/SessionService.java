package com.serch.server.core.session;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.responses.SessionResponse;

import java.util.UUID;

/**
 * Service interface for managing user sessions.
 *
 * @see SessionImplementation
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
     * @param country The country location the user is making the request from.
     * @param state The state location the user is making the request from.
     *
     * @return ApiResponse indicating the validation status.
     *
     * @see ApiResponse
     */
    ApiResponse<String> validateSession(String token, String state, String country);

    /**
     * Validates the authenticity and expiration of a session token.
     *
     * @param token The session token to validate.
     * @return User with the token
     *
     * @see User
     */
    User validate(String token);

    /**
     * Updates the last signed in timestamp of the user.
     */
    void updateLastSignedIn();

    /**
     * Update the details of the session which the request is coming from
     *
     * @param ipAddress The IPAddress making the request
     * @param token The JWT Token
     */
    void updateSessionDetails(String ipAddress, String token);

    /**
     * Signs out the user by revoking all sessions and refresh tokens.
     */
    void signOut();
}