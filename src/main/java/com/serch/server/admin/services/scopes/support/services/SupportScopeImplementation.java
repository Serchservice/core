package com.serch.server.admin.services.scopes.support.services;

import com.serch.server.admin.services.projections.MetricProjection;
import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.Metric;
import com.serch.server.admin.services.scopes.support.responses.SupportScopeResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.repositories.company.ComplaintRepository;
import com.serch.server.repositories.company.SpeakWithSerchRepository;
import com.serch.server.utils.AdminUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportScopeImplementation implements SupportScopeService {
    private final ComplaintRepository complaintRepository;
    private final SpeakWithSerchRepository speakWithSerchRepository;

    @Override
    public ApiResponse<SupportScopeResponse> overview() {
        SupportScopeResponse response = new SupportScopeResponse();

        // Get the current year
        // Fetch complaint metrics
        List<MetricProjection> complaintMetrics = complaintRepository.findComplaintMetrics();
        response.setComplaintMetrics(getMetricResponse(complaintMetrics));

        // Fetch SpeakWithSerch metrics
        List<MetricProjection> speakWithSerchMetrics = speakWithSerchRepository.findSpeakWithSerchMetrics();
        response.setSpeakWithSerchMetrics(getMetricResponse(speakWithSerchMetrics));

        response.setYears(AdminUtil.years());
        response.setComplaintChart(prepareComplaint(AdminUtil.currentYear()));
        response.setSpeakWithSerchChart(prepareSpeakWithSerch(AdminUtil.currentYear()));

        return new ApiResponse<>(response);
    }

    private List<Metric> getMetricResponse(List<MetricProjection> metricProjections) {
        // Get years from 2024 to current year
        List<Integer> years = AdminUtil.years();

        // Map the fetched metrics to a Map with year as key
        Map<Integer, Long> metricsMap = metricProjections.stream()
                .collect(Collectors.toMap(MetricProjection::getHeader, MetricProjection::getCount));

        // Create a list of Metric with default values
        List<Metric> metrics = new ArrayList<>();
        for (Integer year : years) {
            Metric metric = new Metric();
            metric.setHeader(year.toString());
            metric.setCount(metricsMap.getOrDefault(year, 0L).toString());
            metrics.add(metric);
        }

        return metrics;
    }

    private List<ChartMetric> prepareComplaint(Integer year) {
        return Arrays.stream(IssueStatus.values()).map(status -> {
            ChartMetric chartMetric = new ChartMetric();
            chartMetric.setLabel(status.getType());

            long count = complaintRepository.countByYearAndStatus(year, status);
            chartMetric.setValue((int) count);
            return chartMetric;
        }).toList();
    }

    private List<ChartMetric> prepareSpeakWithSerch(Integer year) {
        return Arrays.stream(IssueStatus.values()).map(status -> {
            ChartMetric chartMetric = new ChartMetric();
            chartMetric.setLabel(status.getType());

            long count = speakWithSerchRepository.countByYearAndStatus(year, status);
            chartMetric.setValue((int) count);
            return chartMetric;
        }).toList();
    }

    @Override
    public ApiResponse<List<ChartMetric>> complaint(Integer year) {
        return new ApiResponse<>(prepareComplaint(year));
    }

    @Override
    public ApiResponse<List<ChartMetric>> speakWithSerch(Integer year) {
        return new ApiResponse<>(prepareSpeakWithSerch(year));
    }
}