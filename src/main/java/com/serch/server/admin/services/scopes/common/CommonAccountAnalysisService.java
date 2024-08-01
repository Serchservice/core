package com.serch.server.admin.services.scopes.common;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.auth.User;

import java.util.List;

public interface CommonAccountAnalysisService {
    /**
     * Get the analysis on the user's account status, to see how it has changed over the given year.
     *
     * @param user The {@link User} whose data is being requested for
     * @param year The year in request. Defaults to current year
     *
     * @return List of {@link ChartMetric}
     */
    List<ChartMetric> accountStatus(User user, Integer year);

    /**
     * Get the list of years the user has been in the platform
     *
     * @param user The {@link User} whose data is being requested for
     *
     * @return List of {@link Integer}
     */
    List<Integer> years(User user);

    /**
     * Get the analysis on the user's wallet transactions to see how the user has transacted over the platform
     *
     * @param user The {@link User} whose data is being requested for
     * @param year The year in request. Defaults to current year
     *
     * @return List of {@link ChartMetric}
     */
    List<ChartMetric> wallet(User user, Integer year);

    /**
     * Get the analysis on the user's transactions to view how the user has fared with transaction and their statuses
     * <p></p>
     * @see com.serch.server.enums.transaction.TransactionStatus
     * @see com.serch.server.enums.transaction.TransactionType
     *
     * @param user The {@link User} whose data is being requested for
     * @param year The year in request. Defaults to current year
     *
     * @return List of {@link ChartMetric}
     */
    List<ChartMetric> transaction(User user, Integer year);

    /**
     * Gets the analysis on the product activity of the user.
     * <p></p>
     * @see com.serch.server.models.schedule.Schedule
     * @see com.serch.server.models.trip.Trip
     * @see com.serch.server.models.conversation.Call
     * @see com.serch.server.models.conversation.ChatRoom
     * @see com.serch.server.models.shared.SharedLink
     *
     * @param user The {@link User} whose data is being requested for
     * @param year The year in request. Defaults to current year
     *
     * @return List of {@link ChartMetric}
     */
    List<ChartMetric> activity(User user, Integer year);

    /**
     * Gets the analysis on authentication for the user, to see how the user has logged in to the platform.
     * <p></p>
     * @see com.serch.server.models.auth.Session
     * @see com.serch.server.models.auth.mfa.MFAFactor
     * @see com.serch.server.models.auth.mfa.MFAChallenge
     * @see com.serch.server.bases.BaseDevice
     *
     * @param user The {@link User} whose data is being requested for
     * @param year The year in request. Defaults to current year
     *
     * @return List of {@link ChartMetric}
     */
    List<ChartMetric> auth(User user, Integer year);

    /**
     * Gets the analysis on the user's account to see profile transactions.
     * <p></p>
     * @see com.serch.server.models.bookmark.Bookmark
     * @see com.serch.server.models.rating.Rating
     * @see com.serch.server.models.referral.Referral
     * @see com.serch.server.models.referral.ReferralProgram
     * @see com.serch.server.models.account.AccountReport
     *
     * @param user The {@link User} whose data is being requested for
     * @param year The year in request. Defaults to current year
     *
     * @return List of {@link ChartMetric}
     */
    List<ChartMetric> account(User user, Integer year);

    /**
     * Gets the associate analysis of the user, to see how the associate belonging to the user has grown over the years.
     * Usage > {@link BusinessProfile}
     * and {@link com.serch.server.enums.auth.Role#ASSOCIATE_PROVIDER}
     *
     * @param user The {@link User} whose data is being requested for
     * @param year The year in request. Defaults to current year
     *
     * @return List of {@link ChartMetric}
     */
    List<ChartMetric> associates(User user, Integer year);

    /**
     * Get the subscriptions made by the user to provide more insight on the data
     * <p></p>
     * @see com.serch.server.models.subscription.Subscription
     * @see com.serch.server.models.subscription.SubscriptionInvoice
     *
     * @param user The {@link User} whose data is being requested for
     * @param year The year in request. Defaults to current year
     *
     * @return List of {@link ChartMetric}
     */
    List<ChartMetric> subscriptions(User user, Integer year);

    /**
     * Gets the analysis on the {@link com.serch.server.enums.auth.Role#ASSOCIATE_PROVIDER} list for the
     * {@link BusinessProfile} user over the given year
     * <p></p>
     * @see com.serch.server.models.business.BusinessSubscription
     *
     * @param user The {@link User} whose data is being requested for
     * @param year The year in request. Defaults to current year
     *
     * @return List of {@link ChartMetric}
     */
    List<ChartMetric> subscribed(User user, Integer year);
}