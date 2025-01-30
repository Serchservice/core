package com.serch.server.domains.shared.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.account.responses.AccountResponse;
import com.serch.server.domains.shared.responses.SharedLinkResponse;
import com.serch.server.domains.shared.services.SharedService;
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
@RequestMapping("/guest/shared")
public class SharedController {
    private final SharedService service;

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> accounts() {
        ApiResponse<List<AccountResponse>> response = service.accounts();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/links")
    @PreAuthorize(value = "hasRole('BUSINESS') || hasRole('USER') || hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<List<SharedLinkResponse>>> links(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<SharedLinkResponse>> response = service.links(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/create")
    @PreAuthorize(value = "hasRole('USER')")
    public ResponseEntity<ApiResponse<List<SharedLinkResponse>>> create(
            @RequestParam String id,
            @RequestParam(required = false) Boolean withInvited
    ) {
        ApiResponse<List<SharedLinkResponse>> response = service.create(id, withInvited);
        return new ResponseEntity<>(response, response.getStatus());
    }
}