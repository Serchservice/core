package com.serch.server.admin.services.scopes.features.services.implementations;

import com.serch.server.admin.mappers.CommonMapper;
import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.Metric;
import com.serch.server.admin.services.scopes.features.responses.RequestSharingScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.responses.RequestSharingScopeResponse;
import com.serch.server.admin.services.scopes.features.services.RequestSharingScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.models.trip.TripShare;
import com.serch.server.repositories.trip.TripShareRepository;
import com.serch.server.utils.AdminUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RequestSharingScopeImplementation implements RequestSharingScopeService {
    private final TripShareRepository tripShareRepository;

    @Override
    @Transactional
    public ApiResponse<RequestSharingScopeOverviewResponse> overview() {
        RequestSharingScopeOverviewResponse response = new RequestSharingScopeOverviewResponse();
        response.setYears(AdminUtil.years());
        response.setOverview(summary());
        response.setOfflineChart(offlineChart(null));
        response.setOnlineChart(onlineChart(null));

        return new ApiResponse<>(response);
    }

    private List<Metric> summary() {
        LocalDateTime start = AdminUtil.getStartYear(AdminUtil.currentYear());
        List<Metric> metrics = new ArrayList<>();
        List<TripShare> list = tripShareRepository.findAllByCreatedAtBetween(start, start.plusYears(1));

        Metric metric = new Metric();
        if (!list.isEmpty()) {
            metric.setCount(String.valueOf(list.stream().filter(trip -> trip.getProvider() == null).count()));
            metric.setHeader(String.format("Offline - %s", AdminUtil.currentYear()));
            metric.setFeature("offline");
            metrics.add(metric);

            metric.setCount(String.valueOf(list.stream().filter(trip -> trip.getProvider() != null).count()));
        } else {
            metric.setCount("0");
            metric.setHeader(String.format("Offline - %s", AdminUtil.currentYear()));
            metric.setFeature("offline");
            metrics.add(metric);
        }
        metric.setHeader(String.format("Online - %s", AdminUtil.currentYear()));
        metric.setFeature("online");
        metrics.add(metric);

        return metrics;
    }

    private List<ChartMetric> onlineChart(Integer year) {
        LocalDateTime start = AdminUtil.getStartYear(Objects.requireNonNullElseGet(year, AdminUtil::currentYear));

        List<ChartMetric> metrics = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDateTime startMonth = start.withMonth(month);

            ChartMetric metric = new ChartMetric();
            metric.setLabel(startMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            metric.setColor(AdminUtil.randomColor());
            metric.setValue((int) tripShareRepository.countOnlineWithinDateRange(startMonth, startMonth.plusMonths(1).minusSeconds(1)));
            metrics.add(metric);
        }

        return metrics;
    }

    private List<ChartMetric> offlineChart(Integer year) {
        LocalDateTime start = AdminUtil.getStartYear(Objects.requireNonNullElseGet(year, AdminUtil::currentYear));

        List<ChartMetric> metrics = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDateTime startMonth = start.withMonth(month);

            ChartMetric metric = new ChartMetric();
            metric.setLabel(startMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            metric.setColor(AdminUtil.randomColor());
            metric.setValue((int) tripShareRepository.countOfflineWithinDateRange(startMonth, startMonth.plusMonths(1).minusSeconds(1)));
            metrics.add(metric);
        }

        return metrics;
    }

    @Override
    @Transactional
    public ApiResponse<List<ChartMetric>> fetchOnlineChart(Integer year) {
        return new ApiResponse<>(onlineChart(year));
    }

    @Override
    @Transactional
    public ApiResponse<List<ChartMetric>> fetchOfflineChart(Integer year) {
        return new ApiResponse<>(offlineChart(year));
    }

    @Override
    @Transactional
    public ApiResponse<List<RequestSharingScopeResponse>> onlineList() {
        List<RequestSharingScopeResponse> list = new ArrayList<>();
        List<TripShare> shares = tripShareRepository.findByProviderNotNull();

        if(shares != null && !shares.isEmpty()) {
            list.addAll(shares.stream().sorted(Comparator.comparing(TripShare::getCreatedAt).reversed()).map(share -> {
                RequestSharingScopeResponse response = new RequestSharingScopeResponse();
                response.setShare(share.getId());
                response.setTrip(share.getTrip().getId());
                response.setProfile(CommonMapper.instance.response(share.getProvider()));
                response.setCreatedAt(share.getCreatedAt());
                response.setUpdatedAt(share.getUpdatedAt());
                return response;
            }).toList());
        }

        return new ApiResponse<>(list);
    }

    @Override
    @Transactional
    public ApiResponse<List<RequestSharingScopeResponse>> offlineList() {
        List<RequestSharingScopeResponse> list = new ArrayList<>();
        List<TripShare> shares = tripShareRepository.findByProviderNull();

        if(shares != null && !shares.isEmpty()) {
            list.addAll(shares.stream().sorted(Comparator.comparing(TripShare::getCreatedAt).reversed()).map(share -> {
                RequestSharingScopeResponse response = new RequestSharingScopeResponse();
                response.setShare(share.getId());
                response.setTrip(share.getTrip().getId());
                response.setProfile(CommonMapper.instance.response(share));
                response.getProfile().setName(share.fullName());
                response.setCreatedAt(share.getCreatedAt());
                response.setUpdatedAt(share.getUpdatedAt());
                return response;
            }).toList());
        }

        return new ApiResponse<>(list);
    }
}
