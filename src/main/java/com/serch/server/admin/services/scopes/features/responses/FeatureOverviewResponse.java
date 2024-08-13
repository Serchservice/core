package com.serch.server.admin.services.scopes.features.responses;

import com.serch.server.admin.services.responses.ChartMetric;
import lombok.Data;

import java.util.List;

@Data
public class FeatureOverviewResponse {
    private List<ChartMetric> requestSharing;
    private List<ChartMetric> provideSharing;
    private List<ChartMetric> tip2fix;
    private List<ChartMetric> shops;
    private List<Integer> years;
}