package com.serch.server.admin.services.scopes.features.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.CallScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.responses.CallScopeResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

public interface CallScopeService {
    /**
     * Fetch the overview data for calls in Serch
     *
     * @return {@link ApiResponse} of {@link CallScopeOverviewResponse}
     */
    ApiResponse<CallScopeOverviewResponse> overview();

    /**
     * Fetch the chart data for tip2fix feature in Serch
     *
     * @param year The year being requested in {@link Integer}
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> fetchTip2FixChart(Integer year);

    /**
     * Fetch the chart data for voice call feature in Serch
     *
     * @param year The year being requested in {@link Integer}
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> fetchVoiceChart(Integer year);

    /**
     * Fetch the performance chart data for tip2fix call feature in Serch
     *
     * @param year The year being requested in {@link Integer}
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> fetchTip2FixPerformance(Integer year);

    /**
     * Fetch the list of voice calls in Serch
     *
     * @return {@link ApiResponse} list of {@link CallScopeResponse}
     */
    ApiResponse<List<CallScopeResponse>> voiceCalls();

    /**
     * Fetch the list of tip2fix calls in Serch
     *
     * @return {@link ApiResponse} list of {@link CallScopeResponse}
     */
    ApiResponse<List<CallScopeResponse>> tip2fixCalls();
}
