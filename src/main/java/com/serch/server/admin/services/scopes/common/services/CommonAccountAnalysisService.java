package com.serch.server.admin.services.scopes.common.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.models.auth.User;

import java.util.List;

/**
 * Service interface for performing various analyses related to a user's account activities on the platform.
 * Provides methods to fetch metrics for account status, wallet transactions, general activity, authentication,
 * and more, offering a comprehensive view of the user's engagement over specified years.
 */
public interface CommonAccountAnalysisService {

    /**
     * Analyzes the user's account status to observe changes over a specified year.
     * This method provides metrics that show the account's activity level, such as
     * login frequency, profile updates, and status changes.
     *
     * @param user The {@link User} whose account data is being requested.
     * @param year The year for which the analysis is requested, represented as an {@link Integer}.
     *             If null, the analysis defaults to the current year.
     * @return a list of {@link ChartMetric} objects representing the user's account status metrics for the specified year.
     */
    List<ChartMetric> accountStatus(User user, Integer year);

    /**
     * Retrieves the list of years the user has been active on the platform.
     * This can be used to determine the duration of the user's participation and
     * fetch data relevant to each active year.
     *
     * @param user The {@link User} whose active years are being requested.
     * @return a list of {@link Integer} values representing the years the user has been active on the platform.
     */
    List<Integer> years(User user);

    /**
     * Analyzes the user's wallet transactions over a specified year.
     * This includes tracking the user's payments, credits, and other wallet-related activities,
     * providing insights into financial engagement on the platform.
     *
     * @param user The {@link User} whose wallet data is being requested.
     * @param year The year for which the wallet analysis is requested, represented as an {@link Integer}.
     *             If null, the analysis defaults to the current year.
     * @return a list of {@link ChartMetric} objects representing the user's wallet transaction metrics for the specified year.
     */
    List<ChartMetric> wallet(User user, Integer year);

    /**
     * Analyzes the user's transactions to evaluate performance across various transaction types and statuses.
     * The analysis provides insights into transaction volumes, success rates, and types (e.g., payment, refund).
     *
     * @param user The {@link User} whose transaction data is being requested.
     * @param year The year for which the transaction analysis is requested, represented as an {@link Integer}.
     *             If null, the analysis defaults to the current year.
     * @return a list of {@link ChartMetric} objects representing the user's transaction metrics for the specified year.
     * @see com.serch.server.enums.transaction.TransactionStatus
     * @see com.serch.server.enums.transaction.TransactionType
     */
    List<ChartMetric> transaction(User user, Integer year);

    /**
     * Analyzes the user's platform activity, such as product usage, scheduling, trips, and communication.
     * This metric aggregation helps to understand user engagement trends with various platform features.
     *
     * @param user The {@link User} whose activity data is being requested.
     * @param year The year for which the activity analysis is requested, represented as an {@link Integer}.
     *             If null, the analysis defaults to the current year.
     * @return a list of {@link ChartMetric} objects representing the user's activity metrics for the specified year.
     * @see com.serch.server.models.schedule.Schedule
     * @see com.serch.server.models.trip.Trip
     * @see com.serch.server.models.conversation.Call
     * @see com.serch.server.models.conversation.ChatRoom
     * @see com.serch.server.models.shared.SharedLink
     */
    List<ChartMetric> activity(User user, Integer year);

    /**
     * Analyzes user authentication data to observe login methods, device usage, and multi-factor authentication (MFA).
     * This analysis provides metrics related to user security and authentication patterns on the platform.
     *
     * @param user The {@link User} whose authentication data is being requested.
     * @param year The year for which the authentication analysis is requested, represented as an {@link Integer}.
     *             If null, the analysis defaults to the current year.
     * @return a list of {@link ChartMetric} objects representing the user's authentication metrics for the specified year.
     * @see com.serch.server.models.auth.Session
     * @see com.serch.server.models.auth.mfa.MFAFactor
     * @see com.serch.server.models.auth.mfa.MFAChallenge
     * @see com.serch.server.bases.BaseDevice
     */
    List<ChartMetric> auth(User user, Integer year);

    /**
     * Analyzes the user's account-related activities such as bookmarking, ratings, and referrals.
     * This metric aggregation provides insights into user behavior and interaction with these features on the platform.
     *
     * @param user The {@link User} whose account-related activities are being requested.
     * @param year The year for which the account-related analysis is requested, represented as an {@link Integer}.
     *             If null, the analysis defaults to the current year.
     * @return a list of {@link ChartMetric} objects representing the user's account-related metrics for the specified year.
     * @see com.serch.server.models.bookmark.Bookmark
     * @see com.serch.server.models.rating.Rating
     * @see com.serch.server.models.referral.Referral
     * @see com.serch.server.models.referral.ReferralProgram
     * @see com.serch.server.models.account.AccountReport
     */
    List<ChartMetric> account(User user, Integer year);

    /**
     * Analyzes the growth of the user's associated entities (associates) over the specified year.
     * This analysis helps track the expansion or contraction of the user's network, business affiliations,
     * or role-based relationships on the platform.
     *
     * @param user The {@link User} whose associate growth data is being requested.
     * @param year The year for which the associate analysis is requested, represented as an {@link Integer}.
     *             If null, the analysis defaults to the current year.
     * @return a list of {@link ChartMetric} objects representing the user's associate-related metrics for the specified year.
     * @see com.serch.server.models.account.BusinessProfile
     * @see com.serch.server.enums.auth.Role#ASSOCIATE_PROVIDER
     */
    List<ChartMetric> associates(User user, Integer year);
}