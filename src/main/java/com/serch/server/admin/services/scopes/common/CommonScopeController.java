package com.serch.server.admin.services.scopes.common;

import com.serch.server.bases.ApiResponse;
import com.serch.server.utils.AdminUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scope/common")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class CommonScopeController {
    @GetMapping("/years")
    public ResponseEntity<ApiResponse<List<Integer>>> fetchYears() {
        ApiResponse<List<Integer>> response = new ApiResponse<>("", AdminUtil.years(), HttpStatus.OK);
        return ResponseEntity.ok(response);
    }
}
