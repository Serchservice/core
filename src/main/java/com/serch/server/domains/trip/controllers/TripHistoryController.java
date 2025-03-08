package com.serch.server.domains.trip.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.trip.responses.TripResponse;
import com.serch.server.domains.trip.services.TripHistoryService;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trip/history")
public class TripHistoryController {
    private final TripHistoryService service;
    private final AuthUtil authUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TripResponse>>> history(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, name = "shared") Boolean isShared,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        ApiResponse<List<TripResponse>> response = service.history(authUtil.getGuestId(), authUtil.getLinkId(), page, size, isShared, category, date);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/shared")
    public ResponseEntity<ApiResponse<List<TripResponse>>> shared(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<TripResponse>> response = service.shared(authUtil.getGuestId(), authUtil.getLinkId(), page, size, false, null);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/requested")
    public ResponseEntity<ApiResponse<List<TripResponse>>> requested(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<TripResponse>> response = service.requested(authUtil.getGuestId(), authUtil.getLinkId(), page, size, false, null);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TripResponse>>> active(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<TripResponse>> response = service.active(authUtil.getGuestId(), authUtil.getLinkId(), page, size, false, null);
        return new ResponseEntity<>(response, response.getStatus());
    }
}