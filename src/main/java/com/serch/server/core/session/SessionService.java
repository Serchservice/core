package com.serch.server.core.session;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.responses.SessionResponse;

import java.util.UUID;

/**
 * Service interface for managing user sessions.
 * <p>
 * This interface provides methods for generating, revoking, and validating user sessions,
 * along with handling refresh tokens and updating session details. Implementations of this
 * interface should handle the necessary business logic for session management in the application.
 * </p>
 *
 * @see SessionImplementation
 */
public interface SessionService {

    /**
     * Revokes a refresh token associated with the specified user ID.
     * <p>
     * This method is used to invalidate a refresh token, preventing it from being used to
     * obtain new access tokens for the user. It ensures that the user can no longer maintain
     * an active session using the revoked token.
     * </p>
     *
     * @param refreshTokenId The unique identifier of the refresh token to revoke.
     * @param userId The unique identifier of the user associated with the token.
     */
    void revokeRefreshToken(UUID userId, UUID refreshTokenId);

    /**
     * Revokes a session associated with the specified user ID.
     * <p>
     * This method invalidates an active session for the user, preventing further access
     * with that session ID. It can be used to log out users or terminate sessions for security
     * reasons.
     * </p>
     *
     * @param sessionId The unique identifier of the session to revoke.
     * @param userId The unique identifier of the user associated with the session.
     */
    void revokeSession(UUID userId, UUID sessionId);

    /**
     * Generates a new session based on the provided request.
     * <p>
     * This method creates a new user session by processing the session request data.
     * It returns an API response containing the authentication details, such as access tokens
     * and user information, allowing the user to access protected resources.
     * </p>
     *
     * @param request The session request containing user credentials and other necessary information.
     * @return ApiResponse containing the session response, including the generated tokens and user details.
     *
     * @see RequestSession
     * @see AuthResponse
     * @see ApiResponse
     */
    ApiResponse<AuthResponse> generateSession(RequestSession request);

    /**
     * Refreshes an existing session based on the provided token.
     * <p>
     * This method takes a refresh token and returns a new session response, allowing users
     * to obtain fresh access tokens without needing to re-authenticate. It is useful for maintaining
     * user sessions seamlessly.
     * </p>
     *
     * @param token The refresh token used to refresh the session and obtain new access tokens.
     * @return ApiResponse containing the refreshed session response, including the new tokens.
     *
     * @see ApiResponse
     * @see SessionResponse
     */
    ApiResponse<SessionResponse> refreshSession(String token);

    /**
     * Validates the authenticity and expiration of a session token.
     * <p>
     * This method checks if the provided session token is valid, ensuring it has not expired
     * and is associated with an active session. It returns an API response indicating whether
     * the session is valid, along with any relevant status messages.
     * </p>
     *
     * @param token The session token to validate.
     * @param country The country location from which the user is making the request.
     * @param state The state location from which the user is making the request.
     *
     * @return ApiResponse indicating the validation status and any associated messages.
     *
     * @see ApiResponse
     */
    ApiResponse<String> validateSession(String token, String state, String country);

    /**
     * Updates the last signed-in timestamp of the user.
     * <p>
     * This method records the current time as the last signed-in time for the user,
     * allowing the application to track user activity and session management.
     * </p>
     */
    void updateLastSignedIn();

    /**
     * Updates the details of the session from which the request is coming.
     * <p>
     * This method captures the IP address and token of the request, allowing the
     * application to track session usage and provide insights into user behavior.
     * </p>
     *
     * @param ipAddress The IP address making the request.
     * @param token The JWT token representing the session.
     */
    void updateSessionDetails(String ipAddress, String token);

    /**
     * Signs out the user by revoking all sessions and refresh tokens.
     * <p>
     * This method logs the user out by invalidating all active sessions and refresh tokens
     * associated with the user's account. It ensures that the user can no longer access
     * protected resources until they log in again.
     * </p>
     *
     * @param jwt The JWT string used to decipher the session details and identify the user.
     */
    void signOut(String jwt);
}