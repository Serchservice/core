package com.serch.server.domains.company.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.company.requests.IssueRequest;
import com.serch.server.domains.company.responses.IssueResponse;
import com.serch.server.domains.company.responses.SpeakWithSerchResponse;
import com.serch.server.domains.company.services.SpeakWithSerchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company/speak_with_serch")
public class SpeakWithSerchController {
    private final SpeakWithSerchService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SpeakWithSerchResponse>>> messages(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<SpeakWithSerchResponse>> response = service.messages(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/issues/{ticket}")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> issues(
            @PathVariable("ticket") String ticket,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<IssueResponse>> response = service.issues(ticket, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SpeakWithSerchResponse>> lodge(@RequestBody IssueRequest request) {
        ApiResponse<SpeakWithSerchResponse> response = service.lodgeIssue(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("{ticket}")
    public ResponseEntity<ApiResponse<List<SpeakWithSerchResponse>>> markRead(@PathVariable("ticket") String ticket) {
        ApiResponse<List<SpeakWithSerchResponse>> response = service.markRead(ticket);
        return new ResponseEntity<>(response, response.getStatus());
    }
}