package com.serch.server.admin.services.scopes.auth;

import com.serch.server.admin.services.responses.auth.AccountAuthDeviceResponse;
import com.serch.server.admin.services.responses.auth.AccountSessionResponse;
import com.serch.server.admin.services.scopes.common.services.CommonAuthService;
import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scope/auth")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class AuthScopeController {
    private final CommonAuthService commonAuthService;

    @PatchMapping("/sessions")
    public ResponseEntity<ApiResponse<List<AccountSessionResponse>>> sessions(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountSessionResponse>> response = new ApiResponse<>(commonAuthService.sessions(id, page, size));
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/devices")
    public ResponseEntity<ApiResponse<List<AccountAuthDeviceResponse>>> devices(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountAuthDeviceResponse>> response = new ApiResponse<>(commonAuthService.devices(id, page, size));
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/session/refresh/revoke")
    public ResponseEntity<ApiResponse<List<AccountSessionResponse>>> revokeRefreshToken(@RequestParam UUID id) {
        ApiResponse<List<AccountSessionResponse>> response = commonAuthService.revokeRefreshToken(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/session/revoke")
    public ResponseEntity<ApiResponse<List<AccountSessionResponse>>> revokeSession(@RequestParam UUID id) {
        ApiResponse<List<AccountSessionResponse>> response = commonAuthService.revokeSession(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
