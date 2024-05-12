package com.serch.server.services.auth.services;

/**
 * Service interface for generating tokens.
 *
 * @see com.serch.server.services.auth.services.implementations.TokenImplementation
 */
public interface TokenService {

    /**
     * Generates a one-time password (OTP).
     *
     * @return The generated OTP.
     */
    String generateOtp();

    /**
     * Generates a code of specified length. Uses numbers
     *
     * @param length The length of the code to generate.
     * @return The generated code.
     */
    String generateCode(int length);

    /**
     * Generates a refresh token.
     *
     * @return The generated refresh token.
     */
    String generateRefreshToken();

    /**
     * Generates a code of specified length. Uses characters and numbers
     *
     * @param length The length of the code to generate.
     * @return The generated code.
     */
    String generate(int length);
}