package com.serch.server.services.company.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.company.TeamType;
import com.serch.server.services.company.responses.TeamGroupResponse;
import com.serch.server.services.company.responses.TeamResponse;
import com.serch.server.services.company.services.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company/team")
public class TeamController {
    private final TeamService service;

    @GetMapping("/teams")
    public ResponseEntity<ApiResponse<List<TeamGroupResponse>>> groups() {
        ApiResponse<List<TeamGroupResponse>> response = service.teams();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamResponse>>> groups(@RequestParam TeamType type) {
        ApiResponse<List<TeamResponse>> response = service.teams(type);
        return new ResponseEntity<>(response, response.getStatus());
    }
}