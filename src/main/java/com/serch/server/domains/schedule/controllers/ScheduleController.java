package com.serch.server.domains.schedule.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.schedule.requests.ScheduleDeclineRequest;
import com.serch.server.domains.schedule.requests.ScheduleRequest;
import com.serch.server.domains.schedule.responses.ScheduleResponse;
import com.serch.server.domains.schedule.responses.ScheduleTimeResponse;
import com.serch.server.domains.schedule.services.ScheduleService;
import com.serch.server.domains.trip.responses.TripResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService service;

    @GetMapping("/all/times/{provider}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ScheduleTimeResponse>>> times(@PathVariable("provider") UUID provider) {
        ApiResponse<List<ScheduleTimeResponse>> response = service.times(provider);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ScheduleResponse>> request(@RequestBody ScheduleRequest request) {
        ApiResponse<ScheduleResponse> response = service.schedule(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/decline")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<String>> decline(@RequestBody ScheduleDeclineRequest decline) {
        ApiResponse<String> response = service.decline(decline);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/accept/{id}")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<String>> accept(@PathVariable("id") String id) {
        ApiResponse<String> response = service.accept(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/cancel/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> cancel(@PathVariable("id") String id) {
        ApiResponse<String> response = service.cancel(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/close/{id}")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER') || hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> close(@PathVariable("id") String id) {
        ApiResponse<String> response = service.close(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/start/{id}")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER') || hasRole('USER')")
    public ResponseEntity<ApiResponse<TripResponse>> start(@PathVariable("id") String id) {
        ApiResponse<TripResponse> response = service.start(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
