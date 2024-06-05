package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.AccountReportRequest;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.account.responses.DashboardResponse;
import com.serch.server.services.account.responses.AdditionalInformationResponse;
import com.serch.server.services.account.services.AccountDeleteService;
import com.serch.server.services.account.services.AccountReportService;
import com.serch.server.services.account.services.AccountService;
import com.serch.server.services.account.services.AdditionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountDeleteService deleteService;
    private final AccountReportService reportService;
    private final AdditionalService additionalService;
    private final AccountService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> accounts() {
        ApiResponse<List<AccountResponse>> response = service.accounts();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete() {
        ApiResponse<String> response = deleteService.delete();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/additional")
    @PreAuthorize(value = "hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<AdditionalInformationResponse>> additional() {
        ApiResponse<AdditionalInformationResponse> response = additionalService.view();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/password")
    public ResponseEntity<ApiResponse<String>> lastPasswordUpdateAt() {
        ApiResponse<String> response = service.lastPasswordUpdateAt();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/dashboard")
    @PreAuthorize(value = "hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<DashboardResponse>> dashboard() {
        ApiResponse<DashboardResponse> response = service.dashboard();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/dashboard/business")
    @PreAuthorize(value = "hasRole('BUSINESS')")
    public ResponseEntity<ApiResponse<List<DashboardResponse>>> dashboardBusiness() {
        ApiResponse<List<DashboardResponse>> response = service.dashboards();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/report")
    public ResponseEntity<ApiResponse<String>> report(@RequestBody AccountReportRequest request) {
        ApiResponse<String> response = reportService.report(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/fcm/update")
    public ResponseEntity<ApiResponse<String>> updateFcmToken(@RequestParam String token) {
        ApiResponse<String> response = service.updateFcmToken(token);
        return new ResponseEntity<>(response, response.getStatus());
    }
}