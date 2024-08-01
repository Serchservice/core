package com.serch.server.services.report;

import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account/report")
public class AccountReportController {
    private final AccountReportService service;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> report(@RequestBody AccountReportRequest request) {
        ApiResponse<String> response = service.report(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
