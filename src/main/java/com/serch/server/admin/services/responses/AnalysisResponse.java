package com.serch.server.admin.services.responses;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisResponse {
    private List<ChartMetric> status;
    private List<Integer> years;
    private List<ChartMetric> auth;
}