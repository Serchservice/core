package com.serch.server.admin.services.account.services;

import com.serch.server.admin.services.account.responses.AdminListResponse;
import com.serch.server.admin.services.account.responses.TeamOverviewResponse;
import com.serch.server.admin.services.account.responses.CompanyStructure;
import com.serch.server.bases.ApiResponse;

public interface TeamService {
    /**
     * Get an overview of the Serch teams
     *
     * @return {@link ApiResponse} of {@link TeamOverviewResponse}
     */
    ApiResponse<TeamOverviewResponse> overview();

    /**
     * Get a company structure for the Serch admins
     *
     * @return {@link CompanyStructure}
     */
    CompanyStructure team();

    /**
     * Get the list of admins
     *
     * @return {@link ApiResponse} list of {@link AdminListResponse}
     */
    ApiResponse<AdminListResponse> admins();
}