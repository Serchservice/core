package com.serch.server.admin.services.scopes.admin;

import com.serch.server.admin.services.account.requests.AdminProfileUpdateRequest;
import com.serch.server.admin.services.account.services.AdminActivityService;
import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.admin.requests.ChangeRoleRequest;
import com.serch.server.admin.services.scopes.admin.requests.ChangeStatusRequest;
import com.serch.server.admin.services.scopes.admin.responses.AdminScopeResponse;
import com.serch.server.admin.services.scopes.admin.services.AdminScopeService;
import com.serch.server.admin.services.team.responses.AdminActivityResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.file.requests.FileUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scope/admin")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class AdminScopeController {
    private final AdminScopeService service;
    private final AdminActivityService activityService;

    @GetMapping
    public ResponseEntity<ApiResponse<AdminScopeResponse>> fetch(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<AdminScopeResponse> response = service.fetch(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/auth")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> fetchAuthChart(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer year
    ) {
        ApiResponse<List<ChartMetric>> response = service.fetchAuthChart(id, year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/account/status")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> fetchAccountStatusChart(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer year
    ) {
        ApiResponse<List<ChartMetric>> response = service.fetchAccountStatusChart(id, year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<List<AdminActivityResponse>>> activities(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AdminActivityResponse>> response = new ApiResponse<>(activityService.activities(id, page, size));
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/profile/avatar")
    public ResponseEntity<ApiResponse<String>> changeAvatar(@RequestParam UUID id, @RequestBody FileUploadRequest request) {
        ApiResponse<String> response = service.changeAvatar(request, id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/account/role")
    public ResponseEntity<ApiResponse<String>> changeRole(@RequestBody ChangeRoleRequest request) {
        ApiResponse<String> response = service.changeRole(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/account/status")
    public ResponseEntity<ApiResponse<String>> changeStatus(@RequestBody ChangeStatusRequest request) {
        ApiResponse<String> response = service.changeStatus(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/auth/mfa")
    public ResponseEntity<ApiResponse<Boolean>> toggle(@RequestParam UUID id) {
        ApiResponse<Boolean> response = service.toggle(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam UUID id) {
        ApiResponse<String> response = service.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/profile/update")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
    public ResponseEntity<ApiResponse<String>> update(@RequestBody AdminProfileUpdateRequest request, @RequestParam UUID id) {
        ApiResponse<String> response = service.update(request, id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
