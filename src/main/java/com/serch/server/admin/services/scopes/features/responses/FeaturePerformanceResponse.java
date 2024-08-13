package com.serch.server.admin.services.scopes.features.responses;

import com.serch.server.admin.services.responses.ChartMetric;
import lombok.Data;

import java.util.List;

@Data
public class FeaturePerformanceResponse {
    private List<ChartMetric> performance;
    private List<Integer> years;
}