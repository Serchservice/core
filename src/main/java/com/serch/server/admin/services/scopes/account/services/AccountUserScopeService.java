package com.serch.server.admin.services.scopes.account.services;

import com.serch.server.admin.services.responses.AnalysisResponse;
import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.admin.services.responses.auth.AccountMFAChallengeResponse;
import com.serch.server.admin.services.responses.auth.AccountSessionResponse;
import com.serch.server.admin.services.scopes.account.responses.user.*;
import com.serch.server.bases.ApiResponse;
import com.serch.server.services.schedule.responses.ScheduleTimeResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing user-related operations in the account scope.
 */
public interface AccountUserScopeService {
    /**
     * Fetches the profile information for a user based on their ID and role.
     *
     * @param id   the unique identifier of the user
     * @return {@link ApiResponse} containing {@link AccountUserScopeProfileResponse} with profile details
     */
    ApiResponse<AccountUserScopeProfileResponse> profile(String id);

    /**
     * Fetches authentication details for a user based on their ID.
     *
     * @param id   the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing {@link AccountUserScopeAuthResponse} with authentication details
     */
    ApiResponse<AccountUserScopeAuthResponse> auth(UUID id, Integer page, Integer size);

    /**
     * Fetches the rating details for a user based on their ID.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing {@link AccountUserScopeRatingResponse} with rating details
     */
    ApiResponse<AccountUserScopeRatingResponse> rating(String id, Integer page, Integer size);

    /**
     * Fetches wallet information for a user.
     *
     * @param id the unique identifier of the user
     * @return {@link ApiResponse} containing {@link AccountUserScopeWalletResponse} with wallet details
     */
    ApiResponse<AccountUserScopeWalletResponse> wallet(UUID id);

    /**
     * Fetches certificate details for a user.
     *
     * @param id the unique identifier of the user
     * @return {@link ApiResponse} containing {@link AccountUserScopeCertificateResponse} with certificate details
     */
    ApiResponse<AccountUserScopeCertificateResponse> certificate(UUID id);

    /**
     * Fetches analysis data for a user based on ID.
     *
     * @param id   the unique identifier of the user
     * @return {@link ApiResponse} containing {@link AnalysisResponse} with analysis data
     */
    ApiResponse<AnalysisResponse> analysis(UUID id);

    /**
     * Fetches the transaction history for a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeTransactionResponse} with transaction details
     */
    ApiResponse<List<AccountUserScopeTransactionResponse>> transactions(UUID id, Integer page, Integer size);

    /**
     * Fetches the MFA challenges for a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountMFAChallengeResponse} with MFA challenge details
     */
    ApiResponse<List<AccountMFAChallengeResponse>> challenges(UUID id, Integer page, Integer size);

    /**
     * Fetches the reports made against user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeReportResponse} with report details
     */
    ApiResponse<List<AccountUserScopeReportResponse>> reports(UUID id, Integer page, Integer size);

    /**
     * Fetches the list of associates for a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link CommonProfileResponse} with associate details
     */
    ApiResponse<List<CommonProfileResponse>> associates(UUID id, Integer page, Integer size);

    /**
     * Fetches the list of shops associated with a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeShopResponse} with shop details
     */
    ApiResponse<List<AccountUserScopeShopResponse>> shops(UUID id, Integer page, Integer size);

    /**
     * Fetches the schedule information for a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeScheduleResponse} with schedule details
     */
    ApiResponse<List<AccountUserScopeScheduleResponse>> schedules(UUID id, Integer page, Integer size);

    /**
     * Retrieves available time slots for scheduling appointments with a specific provider.
     *
     * @param id The ID of the provider for whom time slots are to be retrieved.
     * @return A response containing a list of available time slots.
     *
     * @see ScheduleTimeResponse
     */
    ApiResponse<List<ScheduleTimeResponse>> times(UUID id);

    /**
     * Fetches the list of active sessions for a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountSessionResponse} with session details
     */
    ApiResponse<List<AccountSessionResponse>> sessions(UUID id, Integer page, Integer size);

    /**
     * Fetches the list of tickets for a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeTicketResponse} with ticket details
     */
    ApiResponse<List<AccountUserScopeTicketResponse>> tickets(UUID id, Integer page, Integer size);

    /**
     * Fetches the list of referrals for a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing {@link AccountUserScopeReferralResponse} with referral details
     */
    ApiResponse<AccountUserScopeReferralResponse> referral(UUID id, Integer page, Integer size);

    /**
     * Fetches the list of referrals for a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing {@link AccountUserScopeReferralResponse} with referral details
     */
    ApiResponse<List<AccountUserScopeReferralResponse.Referral>> referrals(UUID id, Integer page, Integer size);

    /**
     * Fetches the list of bookmarks for a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeBookmarkResponse} with bookmark details
     */
    ApiResponse<List<AccountUserScopeBookmarkResponse>> bookmarks(UUID id, Integer page, Integer size);

    /**
     * Fetches the call history for a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeCallResponse} with call details
     */
    ApiResponse<List<AccountUserScopeCallResponse>> calls(UUID id, Integer page, Integer size);

    /**
     * Fetches the trip history for a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeTripResponse} with trip details
     */
    ApiResponse<List<AccountUserScopeTripResponse>> trips(String id, Integer page, Integer size);

    /**
     * Fetches the list of chat rooms associated with a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeChatRoomResponse} with chat room details
     */
    ApiResponse<List<AccountUserScopeChatRoomResponse>> chatRooms(UUID id, Integer page, Integer size);

    /**
     * Fetches the shared links associated with a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeSharedLinkResponse} with shared link details
     */
    ApiResponse<List<AccountUserScopeSharedLinkResponse>> sharedLinks(UUID id, Integer page, Integer size);

    /**
     * Fetches the shared accounts associated with a user.
     *
     * @param id    the unique identifier of the user
     * @param page  the page number for pagination
     * @param size  the number of records per page
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeSharedAccountResponse} with shared account details
     */
    ApiResponse<List<AccountUserScopeSharedAccountResponse>> sharedAccounts(String id, Integer page, Integer size);

    /**
     * Fetches authentication chart data for a particular year
     * for the specified user ID. This method provides insights
     * into the authentication patterns of the user over the
     * given year, such as successful logins, failed attempts, etc.
     *
     * @param id The unique identifier of the user in {@link UUID} format.
     *           This ID must correspond to an existing user in the system.
     * @param year The year for which the authentication data is requested.
     *             This should be a valid year (e.g., 2023).
     * @return {@link ApiResponse} containing a list of {@link ChartMetric}
     *         that represents the authentication data for the specified year.
     *         If the year is invalid or the user ID does not exist, an error message is returned.
     */
    ApiResponse<List<ChartMetric>> fetchAuthChart(UUID id, Integer year);

    /**
     * Fetches account status chart data for a particular year
     * related to the specified user ID. This method offers insights
     * into the status changes of the user account over the given year,
     * such as account activation, deactivation, or any other relevant
     * status changes.
     *
     * @param id The unique identifier of the user in {@link UUID} format.
     *           This ID must correspond to an existing user in the system.
     * @param year The year for which the account status data is requested.
     *             This should be a valid year (e.g., 2023).
     * @return {@link ApiResponse} containing a list of {@link ChartMetric}
     *         that represents the account status data for the specified year.
     *         If the year is invalid or the admin ID does not exist, an error message is returned.
     */
    ApiResponse<List<ChartMetric>> fetchAccountStatusChart(UUID id, Integer year);

    /**
     * Fetches activity heatmap data for a particular year
     * related to the specified user ID. This method offers insights
     * into the activities of the user over series of Serchservice features.
     *
     * @param id The unique identifier of the user.
     * @param year The year for which the account status data is requested.
     *             This should be a valid year (e.g., 2023).
     * @return {@link ApiResponse} containing a list of {@link AccountUserScopeActivityResponse}.
     *         If the year is invalid or the admin ID does not exist, an error message is returned.
     */
    ApiResponse<List<AccountUserScopeActivityResponse>> fetchActivity(String id, Integer year);
}