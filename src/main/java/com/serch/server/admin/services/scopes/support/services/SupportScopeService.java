package com.serch.server.admin.services.scopes.support.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.support.responses.SupportScopeResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

/**
 * Service interface for support-related operations in the Serch platform.
 * Provides methods to retrieve overviews of support activity and analyze data for complaints
 * and SpeakWithSerch interactions across specified time periods.
 */
public interface SupportScopeService {

    /**
     * Retrieves a general overview of support activities on the Serch platform.
     * This overview may include statistics such as the total number of support requests,
     * open cases, resolved cases, and other relevant metrics that provide insights
     * into the overall performance and workload of the support team.
     *
     * @return an {@link ApiResponse} containing a {@link SupportScopeResponse}, which provides
     *         a comprehensive summary of support-related data, including counts and status metrics.
     */
    ApiResponse<SupportScopeResponse> overview();

    /**
     * Provides an analysis of complaints on the Serch platform for a specified year or the current year if no year is provided.
     * This method offers insights into complaint patterns, categorizing them based on their resolution status,
     * response times, and ticket handling. It helps to track complaint circulation and understand trends
     * in user feedback over time.
     *
     * @param year an optional parameter representing the year to retrieve data for.
     *             If null, the data for the current year is returned.
     * @return an {@link ApiResponse} containing a list of {@link ChartMetric} objects that represent various
     *         metrics related to complaints, such as the number of complaints per month,
     *         response times, and statuses.
     */
    ApiResponse<List<ChartMetric>> complaint(Integer year);

    /**
     * Provides an analysis of SpeakWithSerch interactions on the Serch platform for a specified year or the current year if no year is provided.
     * This method gives insights into the trends and circulation of SpeakWithSerch tickets, focusing on aspects such as
     * response status, frequency of interactions, and the effectiveness of ticket resolutions. The data can help
     * identify periods of high activity or areas where the support process can be improved.
     *
     * @param year an optional parameter representing the year to retrieve data for.
     *             If null, the data for the current year is returned.
     * @return an {@link ApiResponse} containing a list of {@link ChartMetric} objects that provide metrics
     *         related to SpeakWithSerch interactions, such as the number of interactions per month,
     *         response success rates, and unresolved cases.
     */
    ApiResponse<List<ChartMetric>> speakWithSerch(Integer year);
}