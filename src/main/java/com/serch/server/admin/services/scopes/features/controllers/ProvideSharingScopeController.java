package com.serch.server.admin.services.scopes.features.controllers;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.ProvideSharingScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.services.ProvideSharingScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.services.shared.responses.SharedLinkResponse;
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
@RequestMapping("/scope/sharing/provide")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class ProvideSharingScopeController {
    private final ProvideSharingScopeService service;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<ProvideSharingScopeOverviewResponse>> overview() {
        ApiResponse<ProvideSharingScopeOverviewResponse> response = service.overview();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> chart(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.chart(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SharedLinkResponse>>> list() {
        ApiResponse<List<SharedLinkResponse>> response = service.list();
        return new ResponseEntity<>(response, response.getStatus());
    }
}
