package com.serch.server.admin.services.team.controllers;

import com.serch.server.admin.services.team.responses.AdminListResponse;
import com.serch.server.admin.services.team.responses.TeamOverviewResponse;
import com.serch.server.admin.services.team.services.TeamService;
import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/team")
public class TeamController {
    private final TeamService service;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
    public ResponseEntity<ApiResponse<TeamOverviewResponse>> overview() {
        ApiResponse<TeamOverviewResponse> response = service.overview();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<AdminListResponse>> admins() {
        ApiResponse<AdminListResponse> response = service.admins();
        return new ResponseEntity<>(response, response.getStatus());
    }
}