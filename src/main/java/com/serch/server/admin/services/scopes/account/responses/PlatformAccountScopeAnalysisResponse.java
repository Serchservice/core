package com.serch.server.admin.services.scopes.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.ChartMetric;
import lombok.Data;

import java.util.List;

@Data
public class PlatformAccountScopeAnalysisResponse {
    private Long total;
    private String content;
    private List<Integer> years;
    private List<ChartMetric> metrics;

    @JsonProperty("info_metrics")
    private List<ChartMetric> infoMetrics;
}