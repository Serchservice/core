package com.serch.server.admin.services.scopes.features.services.implementations;

import com.serch.server.admin.mappers.CommonMapper;
import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.Metric;
import com.serch.server.admin.services.scopes.features.responses.CallScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.responses.CallScopeResponse;
import com.serch.server.admin.services.scopes.features.services.CallScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.call.CallType;
import com.serch.server.models.conversation.Call;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.utils.AdminUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.account.SerchCategory.*;
import static com.serch.server.enums.call.CallType.T2F;
import static com.serch.server.enums.call.CallType.VOICE;

@Service
@RequiredArgsConstructor
public class CallScopeImplementation implements CallScopeService {
    private final CallRepository callRepository;

    @Override
    public ApiResponse<CallScopeOverviewResponse> overview() {
        CallScopeOverviewResponse response = new CallScopeOverviewResponse();
        response.setOverview(summary());
        response.setYears(AdminUtil.years());
        response.setTip2fixChart(tip2fixChart(null));
        response.setVoiceCallChart(voiceChart(null));
        response.setTip2fixPerformance(tip2fixPerformance(null));

        return new ApiResponse<>(response);
    }

    private List<Metric> summary() {
        LocalDateTime start = AdminUtil.getStartYear(AdminUtil.currentYear());
        List<Call> calls = callRepository.findAllByCreatedAtBetween(start, start.plusYears(1));
        List<Metric> metrics = new ArrayList<>();

        Metric metric = new Metric();
        if(calls.isEmpty()) {
            metric.setCount("0");
            metric.setHeader(String.format("Tip2Fix - %s", AdminUtil.currentYear()));
            metric.setFeature("t2f");
            metrics.add(metric);
        } else {
            metric.setCount(String.valueOf(calls.stream().filter(call -> !call.isVoice()).count()));
            metric.setHeader(String.format("Tip2Fix - %s", AdminUtil.currentYear()));
            metric.setFeature("t2f");
            metrics.add(metric);

            metric.setCount(String.valueOf(calls.stream().filter(Call::isVoice).count()));
        }
        metric.setHeader(String.format("Voice - %s", AdminUtil.currentYear()));
        metric.setFeature("voice");
        metrics.add(metric);

        return metrics;
    }

    private List<ChartMetric> tip2fixChart(Integer year) {
        return getChartMetrics(T2F, year);
    }

    private List<ChartMetric> getChartMetrics(CallType callType, Integer year) {
        LocalDateTime start = AdminUtil.getStartYear(Objects.requireNonNullElseGet(year, AdminUtil::currentYear));

        List<ChartMetric> metrics = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            LocalDateTime startMonth = start.withMonth(month);

            ChartMetric metric = new ChartMetric();
            metric.setLabel(startMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            metric.setColor(AdminUtil.randomColor());
            metric.setValue((int) callRepository.countByType(callType, startMonth, startMonth.plusMonths(1).minusSeconds(1)));
            metrics.add(metric);
        }

        return metrics;
    }

    private List<ChartMetric> voiceChart(Integer year) {
        return getChartMetrics(VOICE, year);
    }

    private List<ChartMetric> tip2fixPerformance(Integer year) {
        LocalDateTime start= AdminUtil.getStartYear(Objects.requireNonNullElseGet(year, AdminUtil::currentYear));
        List<Call> list = callRepository.findAllByCreatedAtBetween(start, start.plusYears(1))
                .stream().filter(call -> !call.isVoice())
                .toList();
        List<ChartMetric> metrics = new ArrayList<>();


        if(list.isEmpty()) {
            FeatureScopeImplementation.buildEmptyCategoryChartMetrics(metrics, Set.of(PERSONAL_SHOPPER, BUSINESS, GUEST, USER));
        } else {
            Map<SerchCategory, List<Call>> groups = list.stream().collect(Collectors.groupingBy(call -> call.getCalled().getCategory()));
            groups.forEach((category, calls) -> {
                ChartMetric metric = new ChartMetric();
                metric.setLabel(category.getType());
                metric.setImage(category.getImage());
                metric.setColor(AdminUtil.randomColor());
                metric.setValue(calls.size());
                metrics.add(metric);
            });
            FeatureScopeImplementation.updateCategoryChartMetrics(metrics, Set.of(PERSONAL_SHOPPER, BUSINESS, GUEST, USER), groups);
        }

        return metrics;
    }

    @Override
    public ApiResponse<List<ChartMetric>> fetchTip2FixChart(Integer year) {
        return new ApiResponse<>(tip2fixChart(year));
    }

    @Override
    public ApiResponse<List<ChartMetric>> fetchVoiceChart(Integer year) {
        return new ApiResponse<>(voiceChart(year));
    }

    @Override
    public ApiResponse<List<ChartMetric>> fetchTip2FixPerformance(Integer year) {
        return new ApiResponse<>(tip2fixPerformance(year));
    }

    @Override
    public ApiResponse<List<CallScopeResponse>> voiceCalls() {
        return getCallList(VOICE);
    }

    @Override
    public ApiResponse<List<CallScopeResponse>> tip2fixCalls() {
        return getCallList(T2F);
    }

    private ApiResponse<List<CallScopeResponse>> getCallList(CallType callType) {
        List<CallScopeResponse> list = callRepository.findByType(callType)
                .stream().sorted(Comparator.comparing(Call::getCreatedAt).reversed()).map(call -> {
                    CallScopeResponse response = new CallScopeResponse();
                    response.setChannel(call.getChannel());
                    response.setLabel(TimeUtil.formatDay(call.getCreatedAt()));
                    response.setDuration(call.getDuration());
                    response.setOutgoing(false);
                    response.setType(call.getType());
                    response.setStatus(call.getStatus());

                    response.setCaller(CommonMapper.instance.response(call.getCaller()));
                    response.setCalled(CommonMapper.instance.response(call.getCalled()));
                    return response;
                }).toList();

        return new ApiResponse<>(list);
    }
}