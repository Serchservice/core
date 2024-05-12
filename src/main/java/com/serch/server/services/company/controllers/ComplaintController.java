package com.serch.server.services.company.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.ComplaintRequest;
import com.serch.server.services.company.services.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company/complaint")
public class ComplaintController {
    private final ComplaintService service;

    @PostMapping("/complain")
    public ResponseEntity<ApiResponse<String>> complain(@RequestBody ComplaintRequest request) {
        ApiResponse<String> response = service.complain(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}