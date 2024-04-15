package com.serch.server.services.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedPricing;
import com.serch.server.services.shared.requests.CreateGuestRequest;
import com.serch.server.services.shared.requests.VerifyEmailRequest;
import com.serch.server.services.shared.responses.GuestAuthResponse;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.responses.SharedPricingData;

/**
 * Service interface for guest authentication operations.
 *
 * @see com.serch.server.services.shared.services.implementations.GuestAuthImplementation
 */
public interface GuestAuthService {
    /**
     * Verifies a guest authentication link.
     *
     * @param link The authentication link to verify.
     * @return A response containing the verification result.
     */
    ApiResponse<GuestAuthResponse> verifyLink(String link);

    /**
     * Generates a guest response.
     *
     * @param link  The shared link associated with the guest.
     * @param guest The guest information.
     * @return The generated guest response.
     *
     * @see SharedLink
     * @see Guest
     */
    GuestResponse response(SharedLink link, Guest guest);

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
     */
    ApiResponse<String> verifyEmailWithToken(VerifyEmailRequest request);

    /**
     * Requests confirmation of an existing email identity.
     *
     * @param request The request containing the email address to confirm.
     * @return A response indicating the success or failure of the request.
     *
     * @see VerifyEmailRequest
     */
    ApiResponse<String> askToConfirmExistingEmailIdentity(VerifyEmailRequest request);

    /**
     * Confirms an existing email identity using a token.
     *
     * @param request The request containing the email address and token for confirmation.
     * @return A response indicating the success or failure of the confirmation process.
     *
     * @see VerifyEmailRequest
     */
    ApiResponse<String> confirmExistingEmailIdentityWithToken(VerifyEmailRequest request);

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
     * Logs in a guest user.
     *
     * @param request The request containing the email address for login.
     * @return A response containing the guest user information upon successful login.
     *
     * @see VerifyEmailRequest
     */
    ApiResponse<GuestResponse> login(VerifyEmailRequest request);

    /**
     * Creates a guest account from an existing user.
     *
     * @param request The request containing the information to create the guest account.
     * @return A response containing the created guest account information.
     *
     * @see CreateGuestRequest
     */
    ApiResponse<GuestResponse> createFromExistingUser(CreateGuestRequest request);

    /**
     * Adds a guest account to a shared link.
     *
     * @param request The request containing the guest account information.
     * @return A response containing the updated shared link information.
     *
     * @see CreateGuestRequest
     */
    ApiResponse<GuestResponse> addGuestAccountToLink(CreateGuestRequest request);

    /**
     * Retrieves shared pricing data associated with a shared link.
     *
     * @param link    The shared link.
     * @param pricing The shared pricing information.
     * @return The shared pricing data.
     *
     * @see SharedLink
     * @see SharedPricing
     */
    SharedPricingData getSharedPricingData(SharedLink link, SharedPricing pricing);
}
