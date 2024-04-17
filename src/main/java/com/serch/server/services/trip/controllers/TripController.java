package com.serch.server.services.trip.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.trip.requests.*;
import com.serch.server.services.trip.responses.TripHistoryResponse;
import com.serch.server.services.trip.services.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trip")
public class TripController {
    private final TripService service;

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TripHistoryResponse>>> history() {
        ApiResponse<List<TripHistoryResponse>> response = service.history();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<String>> request(@RequestBody TripRequest request) {
        ApiResponse<String> response = service.request(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/accept")
    public ResponseEntity<ApiResponse<String>> accept(@RequestBody TripAcceptRequest accept) {
        ApiResponse<String> response = service.accept(accept);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/decline")
    public ResponseEntity<ApiResponse<String>> decline(@RequestBody TripDeclineRequest decline) {
        ApiResponse<String> response = service.decline(decline);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> cancel(@RequestBody TripCancelRequest cancel) {
        ApiResponse<String> response = service.cancel(cancel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/arrival/announce")
    public ResponseEntity<ApiResponse<String>> announceArrival(@RequestParam String trip, @RequestParam String code) {
        ApiResponse<String> response = service.announceArrival(code, trip);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/leave")
    public ResponseEntity<ApiResponse<String>> leave(@RequestParam String trip) {
        ApiResponse<String> response = service.leave(trip);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/end")
    public ResponseEntity<ApiResponse<String>> end(@RequestParam String trip, @RequestParam(required = false) Integer amount) {
        ApiResponse<String> response = service.end(trip, amount);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/sharing/permit")
    public ResponseEntity<ApiResponse<String>> permitSharing(@RequestParam String trip) {
        ApiResponse<String> response = service.permitSharing(trip);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/invite")
    public ResponseEntity<ApiResponse<String>> invite(@RequestBody TripInviteRequest invite) {
        ApiResponse<String> response = service.invite(invite);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/invite/cancel")
    public ResponseEntity<ApiResponse<String>> cancelInvite(@RequestBody TripCancelRequest cancelInvite) {
        ApiResponse<String> response = service.cancelInvite(cancelInvite);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
