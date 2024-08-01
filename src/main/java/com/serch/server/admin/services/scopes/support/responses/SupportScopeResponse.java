package com.serch.server.admin.services.scopes.support.responses;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.Metric;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SupportScopeResponse {
    private List<ChartMetric> complaintChart = new ArrayList<>();
    private List<ChartMetric> speakWithSerchChart = new ArrayList<>();
    private List<Metric> complaintMetrics = new ArrayList<>();
    private List<Metric> speakWithSerchMetrics = new ArrayList<>();
    private List<Integer> years;
}
