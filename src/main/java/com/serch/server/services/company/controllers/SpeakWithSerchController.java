package com.serch.server.services.company.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.IssueRequest;
import com.serch.server.services.company.responses.SpeakWithSerchResponse;
import com.serch.server.services.company.services.SpeakWithSerchService;
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
    public ResponseEntity<ApiResponse<List<SpeakWithSerchResponse>>> messages() {
        ApiResponse<List<SpeakWithSerchResponse>> response = service.message();
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