package com.serch.server.admin.services.scopes.features.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.RequestSharingScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.responses.RequestSharingScopeResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

public interface RequestSharingScopeService {
    /**
     * Fetch the overview data for requestSharing in Serch
     *
     * @return {@link ApiResponse} of {@link RequestSharingScopeOverviewResponse}
     */
    ApiResponse<RequestSharingScopeOverviewResponse> overview();

    /**
     * Fetch the chart data for online requestShared trips in Serch
     *
     * @param year The year being requested in {@link Integer}
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> fetchOnlineChart(Integer year);

    /**
     * Fetch the chart data for offline requestShared trips in Serch
     *
     * @param year The year being requested in {@link Integer}
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> fetchOfflineChart(Integer year);

    /**
     * Fetch the list of online requestShared trips in Serch
     *
     * @return {@link ApiResponse} list of {@link RequestSharingScopeResponse}
     */
    ApiResponse<List<RequestSharingScopeResponse>> onlineList();

    /**
     * Fetch the list of offline requestShared trips in Serch
     *
     * @return {@link ApiResponse} list of {@link RequestSharingScopeResponse}
     */
    ApiResponse<List<RequestSharingScopeResponse>> offlineList();
}
