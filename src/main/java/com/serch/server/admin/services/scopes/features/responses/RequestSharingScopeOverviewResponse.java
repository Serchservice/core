package com.serch.server.admin.services.scopes.features.responses;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.Metric;
import lombok.Data;

import java.util.List;

@Data
public class RequestSharingScopeOverviewResponse {
    private List<Metric> overview;
    private List<Integer> years;
    private List<ChartMetric> onlineChart;
    private List<ChartMetric> offlineChart;
}
