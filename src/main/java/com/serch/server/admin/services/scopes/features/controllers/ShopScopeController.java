package com.serch.server.admin.services.scopes.features.controllers;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.ShopScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.responses.ShopScopeResponse;
import com.serch.server.admin.services.scopes.features.services.ShopScopeService;
import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scope/shop")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class ShopScopeController {
    private final ShopScopeService service;

    @GetMapping
    public ResponseEntity<ApiResponse<ShopScopeOverviewResponse>> overview() {
        ApiResponse<ShopScopeOverviewResponse> response = service.overview();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> chart(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.chart(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ShopScopeResponse>>> list() {
        ApiResponse<List<ShopScopeResponse>> response = service.list();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/find")
    public ResponseEntity<ApiResponse<ShopScopeResponse>> find(@RequestParam String id) {
        ApiResponse<ShopScopeResponse> response = service.find(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
