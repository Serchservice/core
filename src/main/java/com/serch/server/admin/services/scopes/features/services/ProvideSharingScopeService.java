package com.serch.server.admin.services.scopes.features.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.ProvideSharingScopeOverviewResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.shared.responses.SharedLinkResponse;

import java.util.List;

/**
 * Service interface for managing provideSharing-related features on the Serch platform.
 * Offers methods for fetching overviews, analyzing chart response, and retrieving shared trip details.
 */
public interface ProvideSharingScopeService {

    /**
     * Retrieves an overview of the provideSharing feature on the Serch platform.
     * This overview may include key metrics, statistics, and summaries of provideSharing activities,
     * such as the number of shared trips, user participation, and overall performance.
     *
     * @return an {@link ApiResponse} containing a {@link ProvideSharingScopeOverviewResponse}
     *         that provides aggregated response and insights about provideSharing features.
     */
    ApiResponse<ProvideSharingScopeOverviewResponse> overview();

    /**
     * Retrieves chart response for provideShared trips on the Serch platform for a specified year.
     * This method provides insights into trends and metrics related to shared trips, such as
     * monthly activity, growth patterns, and other relevant performance indicators.
     *
     * @param year the year for which to fetch the response, represented as an {@link Integer}.
     *             If the year is null, response for the current year will be used.
     * @return an {@link ApiResponse} containing a list of {@link ChartMetric} objects
     *         representing the provideShared trips response, useful for visualizing trends.
     */
    ApiResponse<List<ChartMetric>> chart(Integer year);

    /**
     * Retrieves a list of provideShared trips on the Serch platform.
     * The list may include details about each shared trip, such as the participants,
     * date and time, location, shared content, and any relevant links.
     *
     * @return an {@link ApiResponse} containing a list of {@link SharedLinkResponse}
     *         objects representing individual provideShared trips and their associated information.
     */
    ApiResponse<List<SharedLinkResponse>> list();
}