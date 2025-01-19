package com.serch.server.admin.services.scopes.features.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.RequestSharingScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.responses.RequestSharingScopeResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

/**
 * Service interface for managing requestSharing-related features on the Serch platform.
 * Provides methods for retrieving overviews, analyzing chart response, and fetching lists of shared trips,
 * both online and offline, associated with requestSharing activities.
 */
public interface RequestSharingScopeService {

    /**
     * Retrieves an overview of the requestSharing feature on the Serch platform.
     * This overview may include key metrics, summaries, and statistics about requestSharing activities,
     * such as the number of shared requests, user engagement, and performance indicators.
     *
     * @return an {@link ApiResponse} containing a {@link RequestSharingScopeOverviewResponse}
     *         with aggregated response and insights related to the requestSharing feature.
     */
    ApiResponse<RequestSharingScopeOverviewResponse> overview();

    /**
     * Retrieves chart response for online requestShared trips on the Serch platform for a specified year.
     * Provides insights into trends and metrics for online shared requests, such as growth, activity
     * patterns, and other performance indicators over the specified time period.
     *
     * @param year the year for which to fetch the response, represented as an {@link Integer}.
     *             If the year is null, response for the current year will be used.
     * @return an {@link ApiResponse} containing a list of {@link ChartMetric} objects
     *         representing the online requestShared trips response, suitable for trend visualization.
     */
    ApiResponse<List<ChartMetric>> fetchOnlineChart(Integer year);

    /**
     * Retrieves chart response for offline requestShared trips on the Serch platform for a specified year.
     * Provides insights into trends and metrics for offline shared requests, such as growth, activity
     * patterns, and other performance indicators over the specified time period.
     *
     * @param year the year for which to fetch the response, represented as an {@link Integer}.
     *             If the year is null, response for the current year will be used.
     * @return an {@link ApiResponse} containing a list of {@link ChartMetric} objects
     *         representing the offline requestShared trips response, suitable for trend visualization.
     */
    ApiResponse<List<ChartMetric>> fetchOfflineChart(Integer year);

    /**
     * Retrieves a list of online requestShared trips on the Serch platform.
     * The list includes details about each shared trip that was requested online, such as participants,
     * date and time, locations, and other relevant information.
     *
     * @return an {@link ApiResponse} containing a list of {@link RequestSharingScopeResponse}
     *         objects representing individual online requestShared trips and their associated details.
     */
    ApiResponse<List<RequestSharingScopeResponse>> onlineList();

    /**
     * Retrieves a list of offline requestShared trips on the Serch platform.
     * The list includes details about each shared trip that was requested offline, such as participants,
     * date and time, locations, and other relevant information.
     *
     * @return an {@link ApiResponse} containing a list of {@link RequestSharingScopeResponse}
     *         objects representing individual offline requestShared trips and their associated details.
     */
    ApiResponse<List<RequestSharingScopeResponse>> offlineList();
}