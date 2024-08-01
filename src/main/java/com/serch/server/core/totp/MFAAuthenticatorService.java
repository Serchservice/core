package com.serch.server.core.totp;

import com.serch.server.enums.auth.Role;

public interface MFAAuthenticatorService {
    /**
     * Generate a secret key for the user's MFA Authentication setup
     *
     * @return String of generated secret key
     */
    String getRandomSecretKey();

    /**
     * @param secretKey Base32 encoded secret key (may have optional whitespace)
     * @param account The user's account name. e.g. an email address or a username
     * @param issuer The organization managing this account
     *
     * @return String url for qr code
     *
     * @see <a href="https://github.com/google/google-authenticator/wiki/Key-Uri-Format">...</a>
     */
    String getBarCode(String secretKey, String account, String issuer);

    /**
     * Get a TOTP code to help in validating the secret key against authenticator oauth
     *
     * @param secretKey The generated secret key unique to the user
     *
     * @return A TOTP code generated with the secret key
     */
    String getTOTPCode(String secretKey);

    /**
     * Generates a QR Code data in PNG for the user
     *
     * @param secretKey The totp secret key
     * @param emailAddress The email address of the user
     * @param role The {@link Role} of the user
     *
     * @return A qr code in string format PNG
     */
    String getQRCode(String secretKey, String emailAddress, Role role);

    /**
     * Validates the given code against the MFA implementation logic
     *
     * @param code The code from authenticator app
     * @param secretKey The secret key of the user for MFA
     *
     * @return true or false to verify that the code matches the implementation of the user's MFA
     */
    boolean isValid(String code, String secretKey);
}
