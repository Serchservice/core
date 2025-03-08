package com.serch.server.admin.services.scopes.support.controllers;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.support.services.SupportScopeService;
import com.serch.server.admin.services.scopes.support.responses.SupportScopeResponse;
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
@RequestMapping("/api/v1/scope/support")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class SupportScopeController {
    private final SupportScopeService service;

    @GetMapping
    public ResponseEntity<ApiResponse<SupportScopeResponse>> overview() {
        ApiResponse<SupportScopeResponse> response = service.overview();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/complaint")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> complaint(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.complaint(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/speak-with-serch")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> speakWithSerch(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.speakWithSerch(year);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
