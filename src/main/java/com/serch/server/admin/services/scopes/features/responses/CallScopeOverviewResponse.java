package com.serch.server.admin.services.scopes.features.responses;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.Metric;
import lombok.Data;

import java.util.List;

@Data
public class CallScopeOverviewResponse {
    private List<Metric> overview;
    private List<Integer> years;
    private List<ChartMetric> tip2fixChart;
    private List<ChartMetric> voiceCallChart;
    private List<ChartMetric> tip2fixPerformance;
}