package com.serch.server.core.session;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.session.requests.GoSession;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.services.auth.responses.GoAuthResponse;

/**
 * Interface defining methods for managing go-user sessions.
 */
public interface GoSessionService {
    /**
     * Processes a go-user login request and returns a response containing relevant information.
     *
     * @param user The user credentials containing details required for go-user.
     * @return A {@link GoAuthResponse} object containing the go-user session details or an error message.
     */
    GoAuthResponse response(GoUser user);

    /**
     * Validates a go-user session token and returns an API response indicating success or failure.
     *
     * @param token The go-user session token to be validated.
     * @return An {@link ApiResponse} object with a success flag and an optional message.
     */
    ApiResponse<String> validateSession(String token);

    /**
     * Decodes the auth token of the user
     *
     * @param token The auth token that is encrypted
     *
     * @return {@link GoSession} data containing the credentials of the user with this token.
     */
    GoSession decode(String token);
}