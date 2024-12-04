package com.serch.server.admin.services.scopes.account.controllers;

import com.serch.server.admin.services.scopes.account.responses.AccountScopeAnalysisResponse;
import com.serch.server.admin.services.scopes.account.services.AccountScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scope/platform/account")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class AccountScopeController {
    private final AccountScopeService service;

    @GetMapping("/analysis")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchAccountAnalysis(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchAccountAnalysis(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/country")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByCountry(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByCountry(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/timezone")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByTimezone(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByTimezone(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/state")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByState(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByState(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/gender")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByGender(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByGender(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/rating")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByRating(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByRating(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/status")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByAccountStatus(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByAccountStatus(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/trip")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByTripStatus(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByTripStatus(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/certification")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByCertified(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByCertified(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/category")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByCategory(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByCategory(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }
}