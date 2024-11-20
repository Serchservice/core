package com.serch.server.admin.services.scopes.account.controllers;

import com.serch.server.admin.services.scopes.account.responses.PlatformAccountScopeMoreAnalysisResponse;
import com.serch.server.admin.services.scopes.account.services.PlatformAccountScopeAnalysisService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scope/platform/account/analysis")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class PlatformAccountScopeAnalysisController {
    private final PlatformAccountScopeAnalysisService service;

    @GetMapping("/more")
    public ResponseEntity<ApiResponse<PlatformAccountScopeMoreAnalysisResponse>> fetch(@RequestParam(required = false) Role role) {
        ApiResponse<PlatformAccountScopeMoreAnalysisResponse> response = service.fetch(role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/more/{year}")
    public ResponseEntity<ApiResponse<List<PlatformAccountScopeMoreAnalysisResponse>>> fetch(
            @RequestParam(required = false) Role role,
            @PathVariable Integer year
    ) {
        ApiResponse<List<PlatformAccountScopeMoreAnalysisResponse>> response = service.fetch(role, year);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
