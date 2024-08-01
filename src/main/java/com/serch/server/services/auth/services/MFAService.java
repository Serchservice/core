package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.auth.requests.RequestDevice;
import com.serch.server.services.auth.requests.RequestMFAChallenge;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.responses.MFADataResponse;
import com.serch.server.services.auth.responses.MFARecoveryCodeResponse;
import com.serch.server.services.auth.responses.MFAUsageResponse;

import java.util.List;

/**
 * Service interface for managing Multi-Factor Authentication (MFA) operations.
 *
 * @see com.serch.server.services.auth.services.implementations.MFAImplementation
 */
public interface MFAService {

    /**
     * Retrieves MFA data for the current user.
     *
     * @return An API response containing MFA data.
     *
     * @see ApiResponse
     * @see MFADataResponse
     */
    ApiResponse<MFADataResponse> getMFAData();

    /**
     * Retrieves MFA data for the current user.
     *
     * @param user The {@link User} data
     * @return An MFA data.
     *
     * @see MFADataResponse
     */
    MFADataResponse getMFAData(User user);

    /**
     * Validates the MFA challenge code.
     *
     * @param request The MFA challenge request.
     * @return An API response containing authentication response.
     *
     * @see ApiResponse
     * @see AuthResponse
     * @see RequestMFAChallenge
     */
    ApiResponse<AuthResponse> validateCode(RequestMFAChallenge request);

    /**
     * Validates the MFA recovery code.
     *
     * @param request The MFA challenge request.
     * @return An API response containing authentication response.
     *
     * @see ApiResponse
     * @see AuthResponse
     * @see RequestMFAChallenge
     */
    ApiResponse<AuthResponse> validateRecoveryCode(RequestMFAChallenge request);

    /**
     * Retrieves the MFA recovery codes for the current user.
     *
     * @return An API response containing MFA recovery codes.
     *
     * @see ApiResponse
     * @see MFARecoveryCodeResponse
     */
    ApiResponse<List<MFARecoveryCodeResponse>> getRecoveryCodes();

    /**
     * Disables MFA for the current user.
     *
     * @param device The device where the request is made from
     * @return An API response confirming the disablement of MFA.
     *
     * @see ApiResponse
     * @see AuthResponse
     */
    ApiResponse<AuthResponse> disable(RequestDevice device);

    /**
     * Retrieves usage statistics of MFA for the current user.
     *
     * @return An API response containing MFA usage statistics.
     *
     * @see ApiResponse
     * @see MFAUsageResponse
     */
    ApiResponse<MFAUsageResponse> usage();
}