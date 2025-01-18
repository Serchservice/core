package com.serch.server.domains.company.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.company.requests.ServiceSuggestRequest;
import com.serch.server.domains.company.services.SuggestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company/suggest")
public class SuggestController {
    private final SuggestService service;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> suggest(@RequestBody ServiceSuggestRequest request) {
        ApiResponse<String> response = service.suggest(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}