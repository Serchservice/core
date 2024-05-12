package com.serch.server.services.auth.services;

import com.serch.server.services.auth.requests.RequestSessionToken;

import java.util.Map;

/**
 * Service interface for managing JWT (JSON Web Token) operations.
 *
 * @see com.serch.server.services.auth.services.implementations.JwtImplementation
 */
public interface JwtService {

    /**
     * Generates a JWT token based on the provided data.
     *
     * @param data The data for the token.
     * @return The generated JWT token.
     *
     * @see RequestSessionToken
     */
    String generateToken(Map<String, Object> data, String subject);

    /**
     * Generates a JWT token based on the provided session token request.
     *
     * @param tokenRequest The session token request.
     * @return The generated JWT token.
     *
     * @see RequestSessionToken
     */
    String generateToken(RequestSessionToken tokenRequest);

    /**
     * Checks whether the provided JWT token has expired.
     *
     * @param token The JWT token to be checked.
     * @return {@code true} if the token has expired, otherwise {@code false}.
     */
    boolean isTokenExpired(String token);

    /**
     * Retrieves a specific item from the JWT token based on the provided identifier.
     *
     * @param token      The JWT token.
     * @param identifier The identifier of the item to retrieve from the token.
     * @return The item value associated with the provided identifier.
     */
    String getItemFromToken(String token, String identifier);

    /**
     * Checks if the provided JWT token has been issued by the Serch application.
     *
     * @param token The JWT token to be checked.
     * @return {@code true} if Serch have issued the token, otherwise {@code false}.
     */
    boolean isTokenIssuedBySerch(String token);

    /**
     * Retrieves the email address associated with the provided JWT token.
     *
     * @param token The JWT token.
     * @return The email address retrieved from the token.
     */
    String getEmailFromToken(String token);
}