package com.serch.server.services.trip.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.services.trip.requests.OnlineRequest;
import com.serch.server.services.trip.responses.ActiveResponse;
import com.serch.server.services.trip.services.ActiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trip/active")
@RequiredArgsConstructor
public class ActiveController {
    private final ActiveService service;

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<TripStatus>> status() {
        ApiResponse<TripStatus> response = service.fetchStatus();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/toggle")
    public ResponseEntity<ApiResponse<TripStatus>> toggle(@RequestBody OnlineRequest request) {
        ApiResponse<TripStatus> response = service.toggleStatus(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ActiveResponse>>> searchProviders(
            @RequestParam String category, @RequestParam String query,
            @RequestParam Double lng, @RequestParam Double lat,
            @RequestParam(required = false) Double radius
    ) {
        ApiResponse<List<ActiveResponse>> response = service.search(query, category, lng, lat, radius);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search/auto")
    public ResponseEntity<ApiResponse<ActiveResponse>> autoConnect(
            @RequestParam String category, @RequestParam(required = false) String query,
            @RequestParam Double lng, @RequestParam Double lat,
            @RequestParam(required = false) Double radius
    ) {
        ApiResponse<ActiveResponse> response = service.auto(query, category, lng, lat, radius);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
