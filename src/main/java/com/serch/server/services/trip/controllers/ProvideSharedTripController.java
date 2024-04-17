package com.serch.server.services.trip.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.trip.requests.PriceChatRequest;
import com.serch.server.services.trip.requests.ProvideSharedTripRequest;
import com.serch.server.services.trip.requests.ProvideSharedTripCancelRequest;
import com.serch.server.services.trip.responses.PriceDiscussionResponse;
import com.serch.server.services.trip.services.ProvideSharedTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trip/shared")
public class ProvideSharedTripController {
    private final ProvideSharedTripService service;

    @GetMapping("/options")
    public ResponseEntity<ApiResponse<PriceDiscussionResponse>> options(
            @RequestParam(name = "guest_id") String guest,
            @RequestParam(name = "link_id") String link
    ) {
        ApiResponse<PriceDiscussionResponse> response = service.options(guest, link);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<PriceDiscussionResponse>> chat(@RequestBody PriceChatRequest request) {
        ApiResponse<PriceDiscussionResponse> response = service.chat(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<String>> request(@RequestBody ProvideSharedTripRequest request) {
        ApiResponse<String> response = service.request(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/change")
    public ResponseEntity<ApiResponse<String>> change(
            @RequestParam(name = "guest_id") String guest,
            @RequestParam(name = "amount") Integer amount,
            @RequestParam(name = "link_id") String link
    ) {
        ApiResponse<String> response = service.change(amount, guest, link);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> cancel(@RequestBody ProvideSharedTripCancelRequest cancel) {
        ApiResponse<String> response = service.cancel(cancel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/end")
    public ResponseEntity<ApiResponse<String>> end(
            @RequestParam(name = "guest_id") String guest,
            @RequestParam(name = "trip_id") String trip
    ) {
        ApiResponse<String> response = service.end(trip, guest);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
