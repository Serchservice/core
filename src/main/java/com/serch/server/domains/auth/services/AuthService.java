package com.serch.server.domains.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.domains.auth.requests.RequestEmailToken;
import com.serch.server.domains.auth.requests.RequestLogin;
import com.serch.server.domains.auth.requests.RequestProviderProfile;
import com.serch.server.domains.auth.requests.RequestSerchCategory;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.auth.responses.PendingRegistrationResponse;
import jakarta.validation.constraints.NotNull;

/**
 * Service interface for managing authentication-related operations.
 *
 * @see com.serch.server.domains.auth.services.implementations.AuthImplementation
 */
public interface AuthService {

    /**
     * Sends an OTP to the provided email address.
     *
     * @param emailAddress The email address to send the OTP.
     *
     * @see Incomplete
     */
    void sendOtp(String emailAddress);

    /**
     * Checks if the provided email address is valid.
     *
     * @param email The email address to check.
     * @return ApiResponse indicating the result of the check.
     *
     * @see ApiResponse
     */
    ApiResponse<String> checkEmail(String email);

    /**
     * Verifies the OTP sent to the email address.
     *
     * @param request The RequestEmailToken containing the email address and OTP.
     * @return ApiResponse indicating the result of the verification.
     *
     * @see ApiResponse
     * @see RequestEmailToken
     */
    ApiResponse<String> verifyEmailOtp(@NotNull RequestEmailToken request);

    /**
     * Authenticates the user with the provided credentials.
     *
     * @param request The RequestLogin containing the login credentials.
     * @param user    The authenticated user entity.
     * @return ApiResponse containing the authentication response.
     *
     * @see ApiResponse
     * @see RequestLogin
     * @see User
     */
    ApiResponse<AuthResponse> authenticate(RequestLogin request, User user);

    /**
     * This retrieves the {@link AuthResponse} response needed for continuous response request
     *
     * @param request The RequestLogin containing the login credentials.
     * @param user    The authenticated user entity.
     *
     * @return ApiResponse containing the authentication response.
     */
    ApiResponse<AuthResponse> getAuthResponse(RequestLogin request, User user);

    /**
     * Gets a user entity from the incomplete user response.
     *
     * @param incomplete The incomplete user entity.
     * @param role       The role of the user.
     * @return The fully constructed User entity.
     *
     * @see User
     * @see Incomplete
     * @see Role
     */
    User getUserFromIncomplete(Incomplete incomplete, Role role);

    /**
     * Send signup email
     *
     * @param emailAddress The emailAddress receiving the mail
     * @param otp The OTP
     */
    void sendEmail(String emailAddress, String otp);


    /**
     * Verifies the email address of the user to fetch the pending registration token for other registration actions.
     *
     * @param request The {@link RequestLogin} request.
     * @return ApiResponse indicating the status of the profile saving operation with {@link PendingRegistrationResponse}.
     *
     * @see RequestProviderProfile
     * @see ApiResponse
     */
    ApiResponse<PendingRegistrationResponse> verifyPendingRegistration(RequestLogin request);

    /**
     * Saves the profile of a provider.
     *
     * @param request The provider profile request.
     * @return ApiResponse indicating the status of the profile saving operation with {@link PendingRegistrationResponse}.
     *
     * @see RequestProviderProfile
     * @see ApiResponse
     */
    ApiResponse<PendingRegistrationResponse> saveProfile(RequestProviderProfile request);

    /**
     * Saves a category for the provider's services.
     *
     * @param category The request containing the category details.
     * @return ApiResponse indicating the status of the category-saving operation with {@link PendingRegistrationResponse}.
     *
     * @see RequestSerchCategory
     * @see ApiResponse
     */
    ApiResponse<PendingRegistrationResponse> saveCategory(RequestSerchCategory category);
}