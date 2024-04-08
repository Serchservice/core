package com.serch.server.services.company.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.IssueRequest;
import com.serch.server.services.company.services.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issue")
public class IssueController {
    private final IssueService issueService;

    @PostMapping("/lodge")
    public ResponseEntity<ApiResponse<String>> lodgeIssue(@RequestBody IssueRequest request) {
        var response = issueService.lodgeIssue(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
