package com.serch.server.admin.services.scopes.account.controllers;

import com.serch.server.admin.services.scopes.account.responses.AccountScopeMoreAnalysisResponse;
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
@RequestMapping("/scope/account/analysis")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class AccountScopeAnalysisController {
    private final AccountScopeAnalysisService service;

    @GetMapping("/more")
    public ResponseEntity<ApiResponse<AccountScopeMoreAnalysisResponse>> fetch(@RequestParam(required = false) Role role) {
        ApiResponse<AccountScopeMoreAnalysisResponse> response = service.fetch(role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/more/{year}")
    public ResponseEntity<ApiResponse<List<AccountScopeMoreAnalysisResponse>>> fetch(
            @RequestParam(required = false) Role role,
            @PathVariable Integer year
    ) {
        ApiResponse<List<AccountScopeMoreAnalysisResponse>> response = service.fetch(role, year);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
