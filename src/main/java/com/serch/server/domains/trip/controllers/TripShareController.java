package com.serch.server.domains.trip.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.trip.requests.TripAcceptRequest;
import com.serch.server.domains.trip.requests.TripAuthRequest;
import com.serch.server.domains.trip.requests.TripCancelRequest;
import com.serch.server.domains.trip.requests.TripShareRequest;
import com.serch.server.domains.trip.responses.TripResponse;
import com.serch.server.domains.trip.services.TripShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trip/shared")
public class TripShareController {
    private final TripShareService service;

    @PostMapping("/share")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<TripResponse>> share(@RequestBody TripShareRequest request) {
        ApiResponse<TripResponse> response = service.share(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/access")
    public ResponseEntity<ApiResponse<TripResponse>> access(
            @RequestParam(required = false) String guest,
            @RequestParam String id
    ) {
        ApiResponse<TripResponse> response = service.access(guest, id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/accept")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<TripResponse>> accept(@RequestBody TripAcceptRequest request) {
        ApiResponse<TripResponse> response = service.accept(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/auth")
    public ResponseEntity<ApiResponse<TripResponse>> auth(@RequestBody TripAuthRequest request) {
        ApiResponse<TripResponse> response = service.auth(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/leave")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<List<TripResponse>>> leave(@RequestParam String id) {
        ApiResponse<List<TripResponse>> response = service.leave(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/end")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<List<TripResponse>>> end(@RequestParam String id) {
        ApiResponse<List<TripResponse>> response = service.end(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> cancel(@RequestBody TripCancelRequest request) {
        ApiResponse<String> response = service.cancel(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
