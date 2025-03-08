package com.serch.server.domains.company.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.company.services.NewsletterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/company/newsletter")
public class NewsletterController {
    private final NewsletterService service;

    @GetMapping("/subscribe")
    public ResponseEntity<ApiResponse<String>> subscribe(@RequestParam(name = "email_address") String emailAddress) {
        ApiResponse<String> response = service.subscribe(emailAddress);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<String>> unsubscribe(@RequestParam(name = "email_address") String emailAddress) {
        ApiResponse<String> response = service.unsubscribe(emailAddress);
        return ResponseEntity.ok(response);
    }
}
