package com.serch.server.domains.trip.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.trip.requests.*;
import com.serch.server.domains.trip.responses.ActiveResponse;
import com.serch.server.domains.trip.responses.TripResponse;
import com.serch.server.domains.trip.services.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trip")
public class TripController {
    private final TripService service;

    @GetMapping("/pay/service_fee")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<TripResponse>> payServiceFee(@RequestParam String id) {
        ApiResponse<TripResponse> response = service.payServiceFee(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/pay/verify")
    public ResponseEntity<ApiResponse<TripResponse>> verify(
            @RequestParam String id,
            @RequestParam(required = false) String guest,
            @RequestParam String reference
    ) {
        ApiResponse<TripResponse> response = service.verify(id, guest, reference);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<List<ActiveResponse>>> search(
            @RequestParam String phone,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<ActiveResponse>> response = service.search(phone, lat, lng, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/rebook")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TripResponse>> rebook(
            @RequestParam String id,
            @RequestParam(required = false) Boolean withInvited
    ) {
        ApiResponse<TripResponse> response = service.rebook(id, withInvited);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TripResponse>> request(@RequestBody TripInviteRequest request) {
        ApiResponse<TripResponse> response = service.request(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/accept")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<TripResponse>> accept(@RequestBody TripAcceptRequest request) {
        ApiResponse<TripResponse> response = service.accept(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/leave")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<List<TripResponse>>> leave(@RequestParam String id) {
        ApiResponse<List<TripResponse>> response = service.leave(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/end")
    public ResponseEntity<ApiResponse<List<TripResponse>>> end(@RequestBody TripCancelRequest request) {
        ApiResponse<List<TripResponse>> response = service.end(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/auth")
    public ResponseEntity<ApiResponse<TripResponse>> auth(@RequestBody TripAuthRequest request) {
        ApiResponse<TripResponse> response = service.auth(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/cancel")
    public ResponseEntity<ApiResponse<List<TripResponse>>> cancel(@RequestBody TripCancelRequest request) {
        ApiResponse<List<TripResponse>> response = service.cancel(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<TripResponse>> update(@RequestBody TripUpdateRequest request) {
        ApiResponse<TripResponse> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @MessageMapping("/trip/update")
    public void update(@Payload MapViewRequest request, SimpMessageHeaderAccessor header) {
//        socket.authenticate(header);
        service.update(request);
    }
}