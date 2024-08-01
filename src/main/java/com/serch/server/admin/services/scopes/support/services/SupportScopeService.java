package com.serch.server.admin.services.scopes.support.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.support.responses.SupportScopeResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

public interface SupportScopeService {
    /**
     * Get an overview of support in Serch
     *
     * @return {@link ApiResponse} of {@link SupportScopeResponse}
     */
    ApiResponse<SupportScopeResponse> overview();

    /**
     * Get an overview of {@link com.serch.server.models.company.Complaint} in Serch for the given or current year.
     * Understand the circulation of the model based on their response status and ticket response
     *
     * @param year A nullable/optional year to search for
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> complaint(Integer year);

    /**
     * Get an overview of {@link com.serch.server.models.company.SpeakWithSerch} for the given or current year.
     * Understand the circulation of the model based on their response status and ticket response
     *
     * @param year A nullable/optional year to search for
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> speakWithSerch(Integer year);
}
