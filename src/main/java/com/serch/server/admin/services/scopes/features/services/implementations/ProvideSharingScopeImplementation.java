package com.serch.server.admin.services.scopes.features.services.implementations;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.Metric;
import com.serch.server.admin.services.scopes.features.responses.ProvideSharingScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.services.ProvideSharingScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.services.shared.responses.SharedLinkResponse;
import com.serch.server.services.shared.services.SharedService;
import com.serch.server.utils.AdminUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.account.SerchCategory.*;

@Service
@RequiredArgsConstructor
public class ProvideSharingScopeImplementation implements ProvideSharingScopeService {
    private final SharedService sharedService;
    private final SharedLinkRepository sharedLinkRepository;

    @Override
    @Transactional
    public ApiResponse<ProvideSharingScopeOverviewResponse> overview() {
        ProvideSharingScopeOverviewResponse response = new ProvideSharingScopeOverviewResponse();
        response.setYears(AdminUtil.years());
        response.setOverview(summary());
        response.setChart(chartByYear(null));

        return new ApiResponse<>(response);
    }

    private List<Metric> summary() {
        LocalDateTime start = AdminUtil.getStartYear(AdminUtil.currentYear());
        List<Metric> metrics = new ArrayList<>();
        List<SharedLink> list = sharedLinkRepository.findAllByCreatedAtBetween(start, start.plusYears(1));

        if(list.isEmpty()) {
            FeatureScopeImplementation.buildEmptyCategoryMetrics(metrics, Set.of(PERSONAL_SHOPPER, BUSINESS, GUEST, USER));
        } else {
            Map<SerchCategory, List<SharedLink>> groups = list.stream().collect(Collectors.groupingBy(link -> link.getProvider().getCategory()));
            groups.forEach((category, links) -> {
                Metric metric = new Metric();
                metric.setCount(String.valueOf(links.size()));
                metric.setHeader(category.getType());
                metric.setFeature(category.getImage());
                metrics.add(metric);
            });
            FeatureScopeImplementation.updateSharedMetrics(metrics, Set.of(PERSONAL_SHOPPER, BUSINESS, GUEST, USER), groups);
        }

        return metrics;
    }

    private List<ChartMetric> chartByYear(Integer year) {
        LocalDateTime start = AdminUtil.getStartYear(Objects.requireNonNullElseGet(year, AdminUtil::currentYear));

        List<ChartMetric> metrics = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDateTime startMonth = start.withMonth(month);

            ChartMetric metric = new ChartMetric();
            metric.setLabel(startMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            metric.setColor(AdminUtil.randomColor());
            metric.setValue((int) sharedLinkRepository.countWithinDateRange(startMonth, startMonth.plusMonths(1).minusSeconds(1)));
            metrics.add(metric);
        }

        return metrics;
    }

    @Override
    @Transactional
    public ApiResponse<List<ChartMetric>> chart(Integer year) {
        return new ApiResponse<>(chartByYear(year));
    }

    @Override
    @Transactional
    public ApiResponse<List<SharedLinkResponse>> list() {
        List<SharedLinkResponse> list = sharedLinkRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(SharedLink::getUpdatedAt).reversed())
                .map(sharedService::buildLink)
                .toList();
        return new ApiResponse<>(list);
    }
}