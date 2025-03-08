package com.serch.server.admin.services.scopes.features.controllers;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.CallScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.responses.CallScopeResponse;
import com.serch.server.admin.services.scopes.features.services.CallScopeService;
import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scope/call")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class CallScopeController {
    private final CallScopeService service;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<CallScopeOverviewResponse>> overview() {
        ApiResponse<CallScopeOverviewResponse> response = service.overview();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/tip2fix")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> fetchTip2FixChart(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.fetchTip2FixChart(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/voice")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> fetchVoiceChart(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.fetchVoiceChart(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/performance")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> fetchTip2FixPerformance(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.fetchTip2FixPerformance(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/voice")
    public ResponseEntity<ApiResponse<List<CallScopeResponse>>> voiceCalls() {
        ApiResponse<List<CallScopeResponse>> response = service.voiceCalls();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/tip2fix")
    public ResponseEntity<ApiResponse<List<CallScopeResponse>>> tip2fixCalls() {
        ApiResponse<List<CallScopeResponse>> response = service.tip2fixCalls();
        return new ResponseEntity<>(response, response.getStatus());
    }
}
