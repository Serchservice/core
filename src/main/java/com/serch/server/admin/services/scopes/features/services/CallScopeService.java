package com.serch.server.admin.services.scopes.features.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.CallScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.responses.CallScopeResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

/**
 * Service interface for managing call-related features in the Serch platform.
 * Provides methods for fetching call overviews, analyzing performance metrics,
 * and retrieving response on specific call types, such as Tip2Fix and voice calls.
 */
public interface CallScopeService {

    /**
     * Retrieves an overview of call-related response on the Serch platform.
     * This overview may include aggregated statistics such as the total number
     * of calls, the distribution of call types, average call duration, and other
     * relevant metrics that provide a summary of call activity.
     *
     * @return an {@link ApiResponse} containing a {@link CallScopeOverviewResponse}
     *         with detailed statistics and summaries of the call-related features.
     */
    ApiResponse<CallScopeOverviewResponse> overview();

    /**
     * Retrieves chart response for the Tip2Fix feature on the Serch platform for the specified year.
     * This method provides insights into call trends and metrics related to Tip2Fix, such as
     * the number of calls per month, resolution times, and other performance indicators.
     *
     * @param year the year for which to retrieve response, represented as an {@link Integer}.
     *             If the year is null, the current year's response is used by default.
     * @return an {@link ApiResponse} containing a list of {@link ChartMetric} objects
     *         that represent the call response metrics for Tip2Fix, providing a year-over-year comparison.
     */
    ApiResponse<List<ChartMetric>> fetchTip2FixChart(Integer year);

    /**
     * Retrieves chart response for the voice call feature on the Serch platform for the specified year.
     * This method provides insights into voice call trends and metrics, such as call frequency,
     * average call duration, and other performance indicators.
     *
     * @param year the year for which to retrieve response, represented as an {@link Integer}.
     *             If the year is null, the current year's response is used by default.
     * @return an {@link ApiResponse} containing a list of {@link ChartMetric} objects
     *         that represent the call response metrics for voice calls, providing insights into call patterns.
     */
    ApiResponse<List<ChartMetric>> fetchVoiceChart(Integer year);

    /**
     * Retrieves performance chart response for the Tip2Fix call feature on the Serch platform for the specified year.
     * This method offers insights into the efficiency and effectiveness of the Tip2Fix service, highlighting
     * metrics such as call resolution times, success rates, and user satisfaction scores.
     *
     * @param year the year for which to retrieve response, represented as an {@link Integer}.
     *             If the year is null, the current year's response is used by default.
     * @return an {@link ApiResponse} containing a list of {@link ChartMetric} objects
     *         that represent the performance metrics for Tip2Fix calls, aiding in performance analysis.
     */
    ApiResponse<List<ChartMetric>> fetchTip2FixPerformance(Integer year);

    /**
     * Retrieves a list of all voice calls made on the Serch platform.
     * This list may include details such as the caller information, call duration,
     * date and time of the call, and the outcome of the call.
     *
     * @return an {@link ApiResponse} containing a list of {@link CallScopeResponse}
     *         objects that represent individual voice calls and their associated details.
     */
    ApiResponse<List<CallScopeResponse>> voiceCalls();

    /**
     * Retrieves a list of all Tip2Fix calls made on the Serch platform.
     * The list may include information about the calls, such as the user who initiated the call,
     * the duration, the issue being addressed, and the call's resolution status.
     *
     * @return an {@link ApiResponse} containing a list of {@link CallScopeResponse}
     *         objects that represent individual Tip2Fix calls and their associated details.
     */
    ApiResponse<List<CallScopeResponse>> tip2fixCalls();
}