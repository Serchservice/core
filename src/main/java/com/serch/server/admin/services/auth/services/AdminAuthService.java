package com.serch.server.admin.services.auth.services;

import com.serch.server.admin.services.auth.requests.*;
import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.auth.responses.MFADataResponse;

import java.util.UUID;

public interface AdminAuthService {

    /**
     * Authenticates an admin user and provides access to the admin dashboard.
     *
     * @param request The {@link AdminLoginRequest} containing the credentials for admin login.
     * @return {@link ApiResponse} containing a {@link String} message indicating success or failure of the login process.
     */
    ApiResponse<String> login(AdminLoginRequest request);

    /**
     * Creates a new super admin account based on the validated email address.
     *
     * @param request The {@link AdminSignupRequest} containing the required information for creating a super admin account.
     * @return {@link ApiResponse} containing {@link MFADataResponse} which includes MFA information necessary for further verification.
     */
    ApiResponse<MFADataResponse> signup(AdminSignupRequest request);

    /**
     * Creates a new account for an admin, manager, or team member.
     *
     * @param request The {@link AddAdminRequest} containing the information needed to create an admin, manager, or team account.
     * @return {@link ApiResponse} containing a {@link String} message indicating success or failure of the account creation process.
     */
    ApiResponse<String> add(AddAdminRequest request);

    /**
     * Completes the account setup for an existing admin, manager, or team member account.
     *
     * @param request The {@link FinishAdminSetupRequest} containing the information required to finalize the account setup.
     * @return {@link ApiResponse} containing {@link AuthResponse} with authentication details upon successful setup.
     */
    ApiResponse<AuthResponse> finishSignup(FinishAdminSetupRequest request);

    /**
     * Resends an OTP (One Time Password) to the specified admin email address for verification.
     *
     * @param emailAddress The email address to which the OTP will be resent.
     * @return {@link ApiResponse} containing a {@link String} message indicating success or failure of the resend operation.
     */
    ApiResponse<String> resend(String emailAddress);

    /**
     * Confirms the token associated with the link used for inviting a team member to join the admin platform.
     *
     * @param token The token that was included in the invitation link.
     * @return {@link ApiResponse} containing {@link MFADataResponse} which includes MFA information for the invited team member.
     */
    ApiResponse<MFADataResponse> verifyLink(String token);

    /**
     * Confirms the action requests for logging in, finishing signup, or signing up an admin.
     *
     * @param request The {@link AdminAuthTokenRequest} containing the token and action details for confirmation.
     * @return {@link ApiResponse} containing {@link AuthResponse} with the resulting authentication details.
     */
    ApiResponse<AuthResponse> confirm(AdminAuthTokenRequest request);

    /**
     * Resends an invitation to the specified admin email address.
     *
     * @param id The UUID of the user to whom the invite will be resent.
     * @return {@link ApiResponse} containing a {@link String} message indicating success or failure of the resend operation.
     */
    ApiResponse<String> resendInvite(UUID id);

    /**
     * Initiates the password reset process by sending a reset link to the specified user.
     *
     * @param id The UUID of the user requesting the password reset.
     * @return {@link ApiResponse} containing a {@link String} message indicating success or failure of the password reset initiation.
     */
    ApiResponse<String> resetPassword(UUID id);

    /**
     * Verifies the token included in the link sent to the user for resetting their password.
     *
     * @param token The token that was included in the reset link sent to the user.
     * @return {@link ApiResponse} containing a {@link String} message indicating success or failure of the verification process.
     */
    ApiResponse<String> verifyResetLink(String token);

    /**
     * Resets the password of an admin based on the provided request response.
     *
     * @param request The {@link AdminResetPasswordRequest} containing the necessary information for resetting the admin's password.
     * @return {@link ApiResponse} containing {@link AuthResponse} with authentication details upon successful password reset.
     */
    ApiResponse<AuthResponse> resetPassword(AdminResetPasswordRequest request);
}