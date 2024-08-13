package com.serch.server.admin.services.scopes.features.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.ShopScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.responses.ShopScopeResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

public interface ShopScopeService {
    /**
     * Fetch the overview data for shop in Serch
     *
     * @return {@link ApiResponse} of {@link ShopScopeOverviewResponse}
     */
    ApiResponse<ShopScopeOverviewResponse> overview();

    /**
     * Fetch the chart data for shops in Serch
     *
     * @param year The year being requested in {@link Integer}
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> chart(Integer year);

    /**
     * Fetch the list of shops in Serch
     *
     * @return {@link ApiResponse} list of {@link ShopScopeResponse}
     */
    ApiResponse<List<ShopScopeResponse>> list();

    /**
     * Fetch the shop by id
     *
     * @return {@link ApiResponse} of {@link ShopScopeResponse}
     */
    ApiResponse<ShopScopeResponse> find(String id);
}
