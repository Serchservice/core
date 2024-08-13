package com.serch.server.admin.services.scopes.features.services.implementations;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.Metric;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shop.Shop;
import com.serch.server.utils.AdminUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeatureScopeImplementation {
    public static void buildEmptyCategoryMetrics(List<Metric> metrics, Set<SerchCategory> categories) {
        Arrays.stream(SerchCategory.values())
                .filter(category -> categories.stream().noneMatch(cat -> cat == category))
                .forEach(category -> {
                    Metric metric = new Metric();
                    metric.setCount("0");
                    metric.setHeader(category.getType());
                    metric.setFeature(category.getImage());
                    metrics.add(metric);
                });
    }

    public static void updateSharedMetrics(List<Metric> metrics, Set<SerchCategory> categories, Map<SerchCategory, List<SharedLink>> groups) {
        Arrays.stream(SerchCategory.values())
                .filter(category -> categories.stream().noneMatch(cat -> cat == category))
                .filter(category -> !groups.containsKey(category))
                .forEach(category -> {
                    Metric metric = new Metric();
                    metric.setCount("0");
                    metric.setHeader(category.getType());
                    metric.setFeature(category.getImage());
                    metrics.add(metric);
                });
    }

    public static void updateShopMetrics(List<Metric> metrics, Map<SerchCategory, List<Shop>> groups, Set<SerchCategory> categories) {
        Arrays.stream(SerchCategory.values())
                .filter(category -> categories.stream().noneMatch(cat -> cat == category))
                .filter(category -> !groups.containsKey(category))
                .forEach(category -> {
                    Metric metric = new Metric();
                    metric.setCount("0");
                    metric.setHeader(category.getType());
                    metric.setFeature(category.getImage());
                    metrics.add(metric);
                });
    }

    public static void buildEmptyCategoryChartMetrics(List<ChartMetric> metrics, Set<SerchCategory> categories) {
        Arrays.stream(SerchCategory.values())
                .filter(category -> categories.stream().noneMatch(cat -> cat == category))
                .forEach(category -> {
                    ChartMetric metric = new ChartMetric();
                    metric.setLabel(category.getType());
                    metric.setImage(category.getImage());
                    metric.setColor(AdminUtil.randomColor());
                    metric.setValue(0);
                    metrics.add(metric);
                });
    }

    public static void updateCategoryChartMetrics(List<ChartMetric> metrics, Set<SerchCategory> categories, Map<SerchCategory, List<Call>> groups) {
        Arrays.stream(SerchCategory.values())
                .filter(category -> categories.stream().noneMatch(cat -> cat == category))
                .filter(category -> !groups.containsKey(category))
                .forEach(category -> {
                    ChartMetric metric = new ChartMetric();
                    metric.setLabel(category.getType());
                    metric.setImage(category.getImage());
                    metric.setColor(AdminUtil.randomColor());
                    metric.setValue(0);
                    metrics.add(metric);
                });
    }
}