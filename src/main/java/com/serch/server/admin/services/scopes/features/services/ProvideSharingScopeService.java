package com.serch.server.admin.services.scopes.features.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.ProvideSharingScopeOverviewResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.services.shared.responses.SharedLinkResponse;

import java.util.List;

public interface ProvideSharingScopeService {
    /**
     * Fetch the overview data for provideSharing in Serch
     *
     * @return {@link ApiResponse} of {@link ProvideSharingScopeOverviewResponse}
     */
    ApiResponse<ProvideSharingScopeOverviewResponse> overview();

    /**
     * Fetch the chart data for provideShared trips in Serch
     *
     * @param year The year being requested in {@link Integer}
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> chart(Integer year);

    /**
     * Fetch the list of provideShared trips in Serch
     *
     * @return {@link ApiResponse} list of {@link SharedLinkResponse}
     */
    ApiResponse<List<SharedLinkResponse>> list();
}
