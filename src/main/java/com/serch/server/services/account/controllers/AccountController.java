package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.AccountReportRequest;
import com.serch.server.services.account.services.AccountDeleteService;
import com.serch.server.services.account.services.AccountReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountDeleteService deleteService;
    private final AccountReportService reportService;

    @GetMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete() {
        ApiResponse<String> response = deleteService.delete();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/report")
    public ResponseEntity<ApiResponse<String>> report(@RequestBody AccountReportRequest request) {
        ApiResponse<String> response = reportService.report(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
