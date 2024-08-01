package com.serch.server.admin.services.auth;

import com.serch.server.admin.services.auth.requests.*;
import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.responses.MFADataResponse;

import java.util.UUID;

public interface AdminAuthService {
    /**
     * This will give the admin the proper access required for the admin dashboard
     *
     * @param request The {@link AdminLoginRequest} data for admin login
     *
     * @return {@link ApiResponse} of {@link String}
     */
    ApiResponse<String> login(AdminLoginRequest request);

    /**
     * This will create a super admin account based on the email address which is validated
     * for {@link com.serch.server.enums.auth.Role#SUPER_ADMIN} account
     *
     * @param request The {@link AdminSignupRequest} data for creating a super admin account
     *
     * @return {@link ApiResponse} of {@link MFADataResponse}
     */
    ApiResponse<MFADataResponse> signup(AdminSignupRequest request);

    /**
     * This will create an account for either {@link com.serch.server.enums.auth.Role#ADMIN},
     * {@link com.serch.server.enums.auth.Role#MANAGER}, or
     * {@link com.serch.server.enums.auth.Role#TEAM} account
     *
     * @param request The {@link AddAdminRequest} data for creating an admin, manager or team account
     *
     * @return {@link ApiResponse} of {@link String}
     */
    ApiResponse<String> add(AddAdminRequest request);

    /**
     * This will finish the account setup for an existing created {@link com.serch.server.enums.auth.Role#ADMIN},
     * {@link com.serch.server.enums.auth.Role#MANAGER}, or
     * {@link com.serch.server.enums.auth.Role#TEAM} account
     * @param request The {@link FinishAdminSetupRequest} data for account setup
     *
     * @return {@link ApiResponse} of {@link AuthResponse}
     */
    ApiResponse<AuthResponse> finishSignup(FinishAdminSetupRequest request);

    /**
     * This will facilitate the resending of OTP to the admin email address
     *
     * @param emailAddress The email address making the resend request
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> resend(String emailAddress);

    /**
     * This will confirm the token attached to the link used for inviting a team member
     *
     * @param token The token attached to the link sent to the team member
     * @return {@link ApiResponse} of {@link MFADataResponse}
     */
    ApiResponse<MFADataResponse> verifyLink(String token);

    /**
     * This finalizes the {@link AdminAuthService#login(AdminLoginRequest)},
     * {@link AdminAuthService#finishSignup(FinishAdminSetupRequest)}
     * and {@link AdminAuthService#signup(AdminSignupRequest)} action requests.
     *
     * @param request The {@link AdminAuthTokenRequest} request for the action call
     *
     * @return {@link ApiResponse} of {@link AuthResponse}
     */
    ApiResponse<AuthResponse> confirm(AdminAuthTokenRequest request);

    /**
     * This will facilitate the resending of invite to admin email address
     *
     * @param id The user id to send the invite to
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> resendInvite(UUID id);

    /**
     * This will facilitate password reset initialization
     *
     * @param id The user getting the reset password link
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> resetPassword(UUID id);

    /**
     * This will confirm the token attached to the link used for resetting password
     *
     * @param token The token attached to the link sent to the team member
     *
     * @return {@link ApiResponse} of success or failure
     */
    ApiResponse<String> verifyResetLink(String token);

    /**
     * This will reset the password of an admin
     *
     * @param request The {@link AdminResetPasswordRequest} data
     *
     * @return {@link ApiResponse} of {@link AuthResponse}
     */
    ApiResponse<AuthResponse> resetPassword(AdminResetPasswordRequest request);
}