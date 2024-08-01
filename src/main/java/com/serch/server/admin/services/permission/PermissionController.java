package com.serch.server.admin.services.permission;

import com.serch.server.admin.services.permission.requests.PermissionRequest;
import com.serch.server.admin.services.permission.responses.PermissionRequestGroupResponse;
import com.serch.server.admin.services.permission.services.PermissionService;
import com.serch.server.admin.services.scopes.admin.requests.UpdatePermissionRequest;
import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/permission")
public class PermissionController {
    private final PermissionService service;

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
    public ResponseEntity<ApiResponse<String>> grant(@RequestParam Long id) {
        ApiResponse<String> response = service.grant(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/decline")
    public ResponseEntity<ApiResponse<String>> decline(@RequestParam Long id) {
        ApiResponse<String> response = service.decline(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<String>> updatePermissions(@RequestBody UpdatePermissionRequest request) {
        ApiResponse<String> response = service.updatePermissions(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
