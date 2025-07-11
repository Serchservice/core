package com.serch.server.admin.services.account.controllers;

import com.serch.server.admin.services.account.requests.AdminProfileUpdateRequest;
import com.serch.server.admin.services.account.responses.AdminResponse;
import com.serch.server.admin.services.account.services.AdminActivityService;
import com.serch.server.admin.services.account.services.AdminProfileService;
import com.serch.server.admin.services.team.responses.AdminActivityResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.file.requests.FileUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/profile")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class AdminProfileController {
    private final AdminProfileService service;
    private final AdminActivityService activityService;

    @GetMapping
    public ResponseEntity<ApiResponse<AdminResponse>> get() {
        ApiResponse<AdminResponse> response = service.get();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<List<AdminActivityResponse>>> activities(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AdminActivityResponse>> response = new ApiResponse<>(activityService.activities(page, size));
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<AdminResponse>> update(@RequestBody AdminProfileUpdateRequest request) {
        ApiResponse<AdminResponse> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/upload")
    public ResponseEntity<ApiResponse<AdminResponse>> uploadAvatar(@RequestBody FileUploadRequest request) {
        ApiResponse<AdminResponse> response = service.uploadAvatar(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/timezone")
    public ResponseEntity<ApiResponse<String>> updateTimezone(@RequestParam String timezone) {
        ApiResponse<String> response = service.updateTimezone(timezone);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
