package com.serch.server.admin.services.scopes.account.controllers;

import com.serch.server.admin.services.scopes.account.responses.PlatformAccountScopeAnalysisResponse;
import com.serch.server.admin.services.scopes.account.services.PlatformAccountScopeService;
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
public class PlatformAccountScopeController {
    private final PlatformAccountScopeService service;

    @GetMapping("/analysis")
    public ResponseEntity<ApiResponse<PlatformAccountScopeAnalysisResponse>> fetchAccountAnalysis(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<PlatformAccountScopeAnalysisResponse> response = service.fetchAccountAnalysis(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/country")
    public ResponseEntity<ApiResponse<PlatformAccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByCountry(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<PlatformAccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByCountry(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/timezone")
    public ResponseEntity<ApiResponse<PlatformAccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByTimezone(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<PlatformAccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByTimezone(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/state")
    public ResponseEntity<ApiResponse<PlatformAccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByState(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<PlatformAccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByState(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/gender")
    public ResponseEntity<ApiResponse<PlatformAccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByGender(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<PlatformAccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByGender(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/rating")
    public ResponseEntity<ApiResponse<PlatformAccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByRating(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<PlatformAccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByRating(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/status")
    public ResponseEntity<ApiResponse<PlatformAccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByAccountStatus(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<PlatformAccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByAccountStatus(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/trip")
    public ResponseEntity<ApiResponse<PlatformAccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByTripStatus(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<PlatformAccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByTripStatus(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/certification")
    public ResponseEntity<ApiResponse<PlatformAccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByCertified(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<PlatformAccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByCertified(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis/category")
    public ResponseEntity<ApiResponse<PlatformAccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByCategory(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<PlatformAccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByCategory(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }
}