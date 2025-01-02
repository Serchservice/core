package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.DashboardResponse;
import com.serch.server.services.account.services.AccountDashboardService;
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
@RequestMapping("/account/dashboard")
public class AccountDashboardController {
    private final AccountDashboardService service;

    @GetMapping
    @PreAuthorize(value = "hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER') || hasRole('BUSINESS')")
    public ResponseEntity<ApiResponse<DashboardResponse>> dashboard() {
        ApiResponse<DashboardResponse> response = service.dashboard();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/business")
    @PreAuthorize(value = "hasRole('BUSINESS')")
    public ResponseEntity<ApiResponse<List<DashboardResponse>>> dashboardBusiness(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<DashboardResponse>> response = service.dashboards(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }
}