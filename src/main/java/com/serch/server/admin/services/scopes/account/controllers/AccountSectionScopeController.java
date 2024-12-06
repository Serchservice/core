package com.serch.server.admin.services.scopes.account.controllers;

import com.serch.server.admin.services.scopes.account.responses.AccountSectionMetricResponse;
import com.serch.server.admin.services.scopes.account.responses.AccountSectionResponse;
import com.serch.server.admin.services.scopes.account.services.AccountSectionScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scope/account/section")
public class AccountSectionScopeController {
    private final AccountSectionScopeService service;

    @GetMapping("/metric")
    public ResponseEntity<ApiResponse<AccountSectionMetricResponse>> fetch(@RequestParam(required = false) Role role) {
        ApiResponse<AccountSectionMetricResponse> response = service.fetchMetric(role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AccountSectionResponse>> fetchAccounts(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam String alphabet
    ) {
        ApiResponse<AccountSectionResponse> response = service.fetchAccounts(role, page, size, alphabet);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<AccountSectionResponse>> search(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(name = "q") String query
    ) {
        ApiResponse<AccountSectionResponse> response = service.search(role, page, size, query);
        return new ResponseEntity<>(response, response.getStatus());
    }
}