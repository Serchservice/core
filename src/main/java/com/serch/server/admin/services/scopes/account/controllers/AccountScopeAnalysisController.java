package com.serch.server.admin.services.scopes.account.controllers;

import com.serch.server.admin.services.scopes.account.responses.AccountScopeAnalysisResponse;
import com.serch.server.admin.services.scopes.account.responses.AccountScopeMoreAnalysisResponse;
import com.serch.server.admin.services.scopes.account.services.AccountScopeMoreAnalysisService;
import com.serch.server.admin.services.scopes.account.services.AccountScopeAnalysisService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scope/account/analysis")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class AccountScopeAnalysisController {
    private final AccountScopeAnalysisService service;
    private final AccountScopeMoreAnalysisService analysisService;

    @GetMapping
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchAccountAnalysis(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchAccountAnalysis(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/country")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByCountry(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByCountry(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/timezone")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByTimezone(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByTimezone(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/state")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByState(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByState(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/gender")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByGender(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByGender(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/rating")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByRating(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, name = "for_guest") Boolean forGuest
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByRating(year, role, forGuest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByAccountStatus(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByAccountStatus(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/trip")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByTripStatus(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByTripStatus(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/certification")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByCertified(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByCertified(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<AccountScopeAnalysisResponse>> fetchGroupedAccountAnalysisByCategory(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Role role
    ) {
        ApiResponse<AccountScopeAnalysisResponse> response = service.fetchGroupedAccountAnalysisByCategory(year, role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/more")
    public ResponseEntity<ApiResponse<AccountScopeMoreAnalysisResponse>> fetch(@RequestParam(required = false) Role role) {
        ApiResponse<AccountScopeMoreAnalysisResponse> response = analysisService.fetch(role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/more/{year}")
    public ResponseEntity<ApiResponse<List<AccountScopeMoreAnalysisResponse>>> fetch(
            @RequestParam(required = false) Role role,
            @PathVariable Integer year
    ) {
        ApiResponse<List<AccountScopeMoreAnalysisResponse>> response = analysisService.fetch(role, year);
        return new ResponseEntity<>(response, response.getStatus());
    }
}