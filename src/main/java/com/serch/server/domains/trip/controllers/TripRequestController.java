package com.serch.server.domains.trip.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.trip.requests.QuotationRequest;
import com.serch.server.domains.trip.requests.TripAcceptRequest;
import com.serch.server.domains.trip.requests.TripCancelRequest;
import com.serch.server.domains.trip.requests.TripInviteRequest;
import com.serch.server.domains.trip.responses.TripResponse;
import com.serch.server.domains.trip.services.TripRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trip/invite")
public class TripRequestController {
    private final TripRequestService service;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<TripResponse>> request(@RequestBody TripInviteRequest request) {
        ApiResponse<TripResponse> response = service.invite(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/quote")
    public ResponseEntity<ApiResponse<TripResponse>> quote(@RequestBody QuotationRequest request) {
        ApiResponse<TripResponse> response = service.sendQuotation(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<TripResponse>> accept(@RequestBody TripAcceptRequest request) {
        ApiResponse<TripResponse> response = service.accept(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> cancel(@RequestBody TripCancelRequest request) {
        ApiResponse<String> response = service.cancel(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/cancel/quote-{id}")
    public ResponseEntity<ApiResponse<String>> cancel(@RequestBody TripCancelRequest request, @PathVariable Long id) {
        ApiResponse<String> response = service.cancel(request, id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
