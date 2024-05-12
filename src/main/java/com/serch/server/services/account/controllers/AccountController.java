package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.AccountReportRequest;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.account.responses.AdditionalInformationResponse;
import com.serch.server.services.account.services.AccountDeleteService;
import com.serch.server.services.account.services.AccountReportService;
import com.serch.server.services.account.services.AccountService;
import com.serch.server.services.account.services.AdditionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountDeleteService deleteService;
    private final AccountReportService reportService;
    private final AdditionalService additionalService;
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> accounts() {
        ApiResponse<List<AccountResponse>> response = accountService.accounts();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete() {
        ApiResponse<String> response = deleteService.delete();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/additional")
    public ResponseEntity<ApiResponse<AdditionalInformationResponse>> additional() {
        ApiResponse<AdditionalInformationResponse> response = additionalService.view();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/password")
    public ResponseEntity<ApiResponse<String>> lastPasswordUpdateAt() {
        ApiResponse<String> response = accountService.lastPasswordUpdateAt();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/report")
    public ResponseEntity<ApiResponse<String>> report(@RequestBody AccountReportRequest request) {
        ApiResponse<String> response = reportService.report(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}