package com.serch.server.domains.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.account.responses.DashboardResponse;

import java.util.List;

public interface AccountDashboardService {
    /**
     * Fetches the dashboard details for the logged-in user
     *
     * @return {@link ApiResponse} of {@link DashboardResponse}
     */
    ApiResponse<DashboardResponse> dashboard();

    /**
     * Fetches the dashboard response of all business associates
     *
     * @return {@link ApiResponse} of list {@link DashboardResponse}
     */
    ApiResponse<List<DashboardResponse>> dashboards(Integer page, Integer size);
}