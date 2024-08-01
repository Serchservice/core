package com.serch.server.admin.services.scopes.support.controllers;

import com.serch.server.admin.services.scopes.support.responses.SpeakWithSerchOverviewResponse;
import com.serch.server.admin.services.scopes.support.responses.SpeakWithSerchScopeResponse;
import com.serch.server.admin.services.scopes.support.services.SpeakWithSerchScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.IssueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scope/support/speak-with-serch")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class SpeakWithSerchScopeController {
    private final SpeakWithSerchScopeService service;

    @GetMapping
    public ResponseEntity<ApiResponse<SpeakWithSerchOverviewResponse>> overview() {
        ApiResponse<SpeakWithSerchOverviewResponse> response = service.overview();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/find")
    public ResponseEntity<ApiResponse<SpeakWithSerchScopeResponse>> find(@RequestParam String ticket) {
        ApiResponse<SpeakWithSerchScopeResponse> response = service.find(ticket);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/reply")
    public ResponseEntity<ApiResponse<SpeakWithSerchScopeResponse>> reply(@RequestBody IssueRequest request) {
        ApiResponse<SpeakWithSerchScopeResponse> response = service.reply(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/assign")
    public ResponseEntity<ApiResponse<SpeakWithSerchScopeResponse>> assign(@RequestParam UUID id, @RequestParam String ticket) {
        ApiResponse<SpeakWithSerchScopeResponse> response = service.assign(ticket, id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/resolve")
    public ResponseEntity<ApiResponse<SpeakWithSerchScopeResponse>> resolve(@RequestParam String ticket) {
        ApiResponse<SpeakWithSerchScopeResponse> response = service.resolve(ticket);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/close")
    public ResponseEntity<ApiResponse<SpeakWithSerchScopeResponse>> close(@RequestParam String ticket) {
        ApiResponse<SpeakWithSerchScopeResponse> response = service.close(ticket);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
