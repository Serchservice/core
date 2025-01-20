package com.serch.server.domains.schedule.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.schedule.responses.ScheduleGroupResponse;
import com.serch.server.domains.schedule.responses.ScheduleResponse;
import com.serch.server.domains.schedule.services.ScheduleHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule/history")
public class ScheduleHistoryController {
    private final ScheduleHistoryService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleGroupResponse>>> history(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date
    ) {
        ApiResponse<List<ScheduleGroupResponse>> response = service.history(page, size, status, category, date);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> active(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<ScheduleResponse>> response = service.active(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/requested")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> requested(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<ScheduleResponse>> response = service.requested(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }
}