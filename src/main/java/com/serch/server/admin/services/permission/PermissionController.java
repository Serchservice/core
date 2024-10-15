package com.serch.server.admin.services.permission;

import com.serch.server.admin.services.account.responses.AdminTeamResponse;
import com.serch.server.admin.services.permission.requests.PermissionRequest;
import com.serch.server.admin.services.permission.responses.PermissionAccountSearchResponse;
import com.serch.server.admin.services.permission.responses.PermissionRequestGroupResponse;
import com.serch.server.admin.services.permission.responses.PermissionScopeResponse;
import com.serch.server.admin.services.permission.services.PermissionService;
import com.serch.server.admin.services.permission.requests.UpdatePermissionRequest;
import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/permission")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MANAGER', 'TEAM')")
public class PermissionController {
    private final PermissionService service;

    @GetMapping("/scopes")
    public ResponseEntity<ApiResponse<List<PermissionScopeResponse>>> getAllScopes() {
        ApiResponse<List<PermissionScopeResponse>> response = service.getAllScopes();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PermissionAccountSearchResponse>> search(@RequestParam String id) {
        ApiResponse<PermissionAccountSearchResponse> response = service.search(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<PermissionRequestGroupResponse>>> requests() {
        ApiResponse<List<PermissionRequestGroupResponse>> response = service.requests();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<String>> request(@RequestBody PermissionRequest request) {
        ApiResponse<String> response = service.request(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/grant")
    public ResponseEntity<ApiResponse<List<PermissionRequestGroupResponse>>> grant(@RequestParam Long id, @RequestParam(required = false) String expiration) {
        ApiResponse<List<PermissionRequestGroupResponse>> response = service.grant(id, expiration);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/decline")
    public ResponseEntity<ApiResponse<List<PermissionRequestGroupResponse>>> decline(@RequestParam Long id) {
        ApiResponse<List<PermissionRequestGroupResponse>> response = service.decline(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/cancel")
    public ResponseEntity<ApiResponse<List<PermissionRequestGroupResponse>>> cancel(@RequestParam Long id) {
        ApiResponse<List<PermissionRequestGroupResponse>> response = service.cancel(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/revoke")
    public ResponseEntity<ApiResponse<List<PermissionRequestGroupResponse>>> revoke(@RequestParam Long id) {
        ApiResponse<List<PermissionRequestGroupResponse>> response = service.revoke(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<AdminTeamResponse>> updatePermissions(@RequestBody UpdatePermissionRequest request) {
        ApiResponse<AdminTeamResponse> response = service.updatePermissions(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
