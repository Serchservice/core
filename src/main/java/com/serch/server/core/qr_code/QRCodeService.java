package com.serch.server.core.qr_code;

import com.serch.server.enums.auth.Role;

/**
 * Service interface for generating QR codes used in various parts of the platform,
 * such as certificates for businesses and employee business cards.
 */
public interface QRCodeService {

    /**
     * Generates a QR code URL for the platform certificate.
     * This QR code appears on certificates given to businesses and providers
     * for verification purposes. It includes a secret that is used to verify
     * the authenticity of the certificate.
     *
     * @param secret A unique secret associated with the certificate.
     *               This secret is used to verify the certificate when the QR code is scanned.
     * @return A URL as a string representing the QR code that can be used on the certificate.
     */
    String platformCertificate(String secret);

    /**
     * Generates a QR code URL for the details of an administrator (employee).
     * This QR code is used on the employee's business card to provide
     * quick access to their contact details and other relevant information.
     *
     * @return A URL as a string representing the QR code for the employee's business card.
     */
    String generateAdminDetails(String secret);

    /**
     * Generates a QR Code image data in PNG format for the user.
     * <p>
     * This method creates a QR code using the provided TOTP secret key, user's email address, and role.
     * The QR code can be scanned by an authenticator app for easy MFA setup.
     * </p>
     *
     * @param secretKey    The TOTP secret key for MFA.
     * @param emailAddress The email address of the user.
     * @param role        The {@link Role} of the user, indicating their access level within the system.
     *
     * @return A {@link String} representing the QR code in PNG format.
     */
    String generateMFA(String secretKey, String emailAddress, Role role);
}