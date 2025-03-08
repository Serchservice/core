package com.serch.server.admin.services.scopes.features.controllers;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.RequestSharingScopeResponse;
import com.serch.server.admin.services.scopes.features.responses.RequestSharingScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.services.RequestSharingScopeService;
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
@RequestMapping("/api/v1/scope/sharing/request")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class RequestSharingScopeController {
    private final RequestSharingScopeService service;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<RequestSharingScopeOverviewResponse>> overview() {
        ApiResponse<RequestSharingScopeOverviewResponse> response = service.overview();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/online")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> fetchOnlineChart(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.fetchOnlineChart(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/offline")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> fetchOfflineChart(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.fetchOfflineChart(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/online")
    public ResponseEntity<ApiResponse<List<RequestSharingScopeResponse>>> onlineList() {
        ApiResponse<List<RequestSharingScopeResponse>> response = service.onlineList();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/offline")
    public ResponseEntity<ApiResponse<List<RequestSharingScopeResponse>>> offlineList() {
        ApiResponse<List<RequestSharingScopeResponse>> response = service.offlineList();
        return new ResponseEntity<>(response, response.getStatus());
    }
}
