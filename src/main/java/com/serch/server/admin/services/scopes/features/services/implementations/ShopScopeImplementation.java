package com.serch.server.admin.services.scopes.features.services.implementations;

import com.serch.server.admin.mappers.CommonMapper;
import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.Metric;
import com.serch.server.admin.services.scopes.features.responses.ShopScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.responses.ShopScopeResponse;
import com.serch.server.admin.services.scopes.features.services.ShopScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.exceptions.others.ShopException;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.shop.Shop;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.services.shop.services.ShopService;
import com.serch.server.utils.AdminUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.account.SerchCategory.*;

@Service
@RequiredArgsConstructor
public class ShopScopeImplementation implements ShopScopeService {
    private final ShopService shopService;
    private final ShopRepository shopRepository;
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;

    @Override
    public ApiResponse<ShopScopeOverviewResponse> overview() {
        ShopScopeOverviewResponse response = new ShopScopeOverviewResponse();
        response.setYears(AdminUtil.years());
        response.setOverview(summary());
        response.setChart(chartByYear(null));

        return new ApiResponse<>(response);
    }

    private List<Metric> summary() {
        LocalDateTime start = AdminUtil.getStartYear(AdminUtil.currentYear());
        List<Shop> list = shopRepository.findAllByCreatedAtBetween(start, start.plusYears(1));
        List<Metric> metrics = new ArrayList<>();

        if(list.isEmpty()) {
            FeatureScopeImplementation.buildEmptyCategoryMetrics(metrics, Set.of(PERSONAL_SHOPPER, GUEST, USER));
        } else {
            Map<SerchCategory, List<Shop>> groups = list.stream().collect(Collectors.groupingBy(Shop::getCategory));
            groups.forEach((category, shops) -> {
                Metric metric = new Metric();
                metric.setCount(String.valueOf(shops.size()));
                metric.setHeader(category.getType());
                metric.setFeature(category.getImage());
                metrics.add(metric);
            });
            FeatureScopeImplementation.updateShopMetrics(metrics, groups, Set.of(PERSONAL_SHOPPER, GUEST, USER));
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
            metric.setValue((int) shopRepository.countByDateRange(startMonth, startMonth.plusMonths(1).minusSeconds(1)));
            metrics.add(metric);
        }

        return metrics;
    }

    @Override
    public ApiResponse<List<ChartMetric>> chart(Integer year) {
        return new ApiResponse<>(chartByYear(year));
    }

    @Override
    public ApiResponse<List<ShopScopeResponse>> list() {
        List<Shop> list = shopRepository.findAll();

        if(list.isEmpty()) {
            return new ApiResponse<>(Collections.emptyList());
        }

        List<ShopScopeResponse> shops = list.stream()
                .sorted(Comparator.comparing(Shop::getCreatedAt).reversed())
                .map(this::getShopScopeResponse).toList();

        return new ApiResponse<>(shops);
    }

    private ShopScopeResponse getShopScopeResponse(Shop shop) {
        ShopScopeResponse response = new ShopScopeResponse();
        response.setShop(shopService.response(shop));
        response.setProfile(CommonMapper.instance.response(shop.getUser()));
        response.getProfile().setAvatar(profileRepository.findById(shop.getUser().getId())
                .map(Profile::getAvatar)
                .orElse(businessProfileRepository.findById(shop.getUser().getId())
                        .map(BusinessProfile::getAvatar)
                        .orElse("")
                )
        );
        response.setCreatedAt(shop.getCreatedAt());
        response.setUpdatedAt(shop.getUpdatedAt());

        return response;
    }

    @Override
    public ApiResponse<ShopScopeResponse> find(String id) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new ShopException("Shop not found"));

        return new ApiResponse<>(getShopScopeResponse(shop));
    }
}
