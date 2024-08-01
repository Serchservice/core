package com.serch.server.admin.services.scopes.countries_in_serch.responses;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.Metric;
import lombok.Data;

import java.util.List;

@Data
public class CountriesInSerchResponse {
    private List<ChartMetric> launchedCountryChart;
    private List<ChartMetric> launchedStateChart;
    private List<ChartMetric> requestedCountryChart;
    private List<ChartMetric> requestedStateChart;
    private List<Metric> launchedMetrics;
    private List<Metric> requestedMetrics;
    private List<Integer> years;
}