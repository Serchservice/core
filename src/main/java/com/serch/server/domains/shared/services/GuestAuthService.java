package com.serch.server.domains.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.shared.requests.CreateGuestFromUserRequest;
import com.serch.server.domains.shared.requests.CreateGuestRequest;
import com.serch.server.domains.shared.requests.VerifyEmailRequest;
import com.serch.server.domains.shared.responses.GuestResponse;
import com.serch.server.domains.shared.responses.SharedLinkData;

/**
 * Service interface for guest authentication operations.
 *
 * @see com.serch.server.domains.shared.services.implementations.GuestAuthImplementation
 */
public interface GuestAuthService {
    /**
     * Verifies a guest authentication link.
     *
     * @param content The authentication content to verify.
     * @return A response containing the verification result.
     *
     * @see SharedLinkData
     */
    ApiResponse<SharedLinkData> verifyLink(String content);

    /**
     * Requests verification of an email address.
     *
     * @param request The request containing the email address to verify.
     * @return A response indicating the success or failure of the request.
     *
     * @see VerifyEmailRequest
     */
    ApiResponse<String> askToVerifyEmail(VerifyEmailRequest request);

    /**
     * Verifies an email address using a token.
     *
     * @param request The request containing the email address and token for verification.
     * @return A response indicating the success or failure of the verification process.
     *
     * @see VerifyEmailRequest
     * @see GuestResponse
     */
    ApiResponse<GuestResponse> verifyEmailWithToken(VerifyEmailRequest request);

    /**
     * Logs in a guest user.
     *
     * @param request The request containing the email address for login.
     * @return A response containing the guest user information upon successful login.
     *
     * @see VerifyEmailRequest
     */
    ApiResponse<GuestResponse> login(VerifyEmailRequest request);

    /**
     * Creates a guest account.
     *
     * @param request The request containing the guest account information.
     * @return A response containing the created guest account information.
     *
     * @see CreateGuestRequest
     */
    ApiResponse<GuestResponse> create(CreateGuestRequest request);

    /**
     * Creates a guest account from an existing user.
     *
     * @param request The request containing the information to create the guest account.
     * @return A response containing the created guest account information.
     *
     * @see CreateGuestFromUserRequest
     */
    ApiResponse<GuestResponse> createFromExistingUser(CreateGuestFromUserRequest request);
}