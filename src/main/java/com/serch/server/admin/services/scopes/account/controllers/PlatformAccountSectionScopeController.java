package com.serch.server.admin.services.scopes.account.controllers;

import com.serch.server.admin.services.responses.Metric;
import com.serch.server.admin.services.scopes.account.responses.PlatformAccountSectionResponse;
import com.serch.server.admin.services.scopes.account.services.PlatformAccountSectionScopeService;
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
@RequestMapping("/scope/platform/account/section")
public class PlatformAccountSectionScopeController {
    private final PlatformAccountSectionScopeService service;

    @GetMapping("/metric")
    public ResponseEntity<ApiResponse<Metric>> fetch(@RequestParam(required = false) Role role) {
        ApiResponse<Metric> response = service.fetchMetric(role);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PlatformAccountSectionResponse>> fetchAccounts(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam String alphabet
    ) {
        ApiResponse<PlatformAccountSectionResponse> response = service.fetchAccounts(role, page, size, alphabet);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PlatformAccountSectionResponse>> search(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(name = "q") String query
    ) {
        ApiResponse<PlatformAccountSectionResponse> response = service.search(role, page, size, query);
        return new ResponseEntity<>(response, response.getStatus());
    }
}