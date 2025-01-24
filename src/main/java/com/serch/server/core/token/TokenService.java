package com.serch.server.core.token;

import com.serch.server.core.token.implementations.TokenImplementation;

/**
 * Service interface for generating tokens and codes.
 * This interface provides methods for generating one-time passwords (OTPs),
 * numeric codes, alphanumeric codes, and refresh tokens. Implementations
 * of this interface should ensure secure and random generation of these
 * tokens for various use cases such as authentication and authorization.
 *
 * @see TokenImplementation
 */
public interface TokenService {

    /**
     * Generates a one-time password (OTP).
     * <p>
     * The generated OTP is typically used for multi-factor authentication
     * and is valid for a short period of time. This method should ensure
     * that the OTP is securely generated to prevent prediction or forgery.
     * </p>
     *
     * @return The generated OTP as a {@link String}. The length and
     *         characteristics of the OTP may vary depending on the
     *         implementation but is usually numeric and between 6 to 8 digits.
     */
    String generateOtp();

    /**
     * Generates a numeric code of specified length.
     * <p>
     * This method creates a code that consists solely of numeric characters.
     * It is commonly used for scenarios where a simple numeric code is needed,
     * such as verification codes, PINs, or short-term authentication codes.
     * </p>
     *
     * @param length The length of the code to generate.
     *               Must be a positive integer, with a reasonable upper limit
     *               to ensure the generated code remains manageable and usable.
     * @return The generated code as a {@link String}. The output will contain
     *         the specified number of numeric characters.
     */
    String generateCode(int length);

    /**
     * Generates a refresh token.
     * <p>
     * Refresh tokens are used to obtain new access tokens without
     * requiring the user to re-authenticate. This method should ensure that
     * the refresh token is generated securely and is valid for a specified
     * duration.
     * </p>
     *
     * @return The generated refresh token as a {@link String}. The refresh
     *         token should be securely stored and managed to prevent misuse.
     */
    String generateRefreshToken();

    /**
     * Generates an alphanumeric code of specified length.
     * <p>
     * This method creates a code that includes both characters and numbers,
     * allowing for a more complex and secure code generation. It is useful for
     * scenarios that require unique identifiers or codes with enhanced security.
     * </p>
     *
     * @param length The length of the code to generate.
     *               Must be a positive integer to ensure a valid code is produced.
     * @return The generated code as a {@link String}. The output will contain
     *         the specified number of alphanumeric characters.
     */
    String generate(int length);

    /**
     * Generates an alphanumeric code of specified length using specified letters.
     * <p>
     * This method creates a code that includes both characters and numbers,
     * allowing for a more complex and secure code generation. It is useful for
     * scenarios that require unique identifiers or codes with enhanced security.
     * </p>
     *
     * @param from The characters to use in generation
     * @param length The length of the code to generate.
     *               Must be a positive integer to ensure a valid code is produced.
     * @return The generated code as a {@link String}. The output will contain
     *         the specified number of alphanumeric characters.
     */
    String generate(String from, int length);
}