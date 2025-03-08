package com.serch.server.domains.nearby.services.rating.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.rating.requests.GoActivityRatingRequest;
import com.serch.server.domains.nearby.services.rating.responses.GoActivityRatingResponse;
import com.serch.server.domains.nearby.services.rating.services.GoActivityRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/go/rating")
public class GoActivityRatingController {
    private final GoActivityRatingService service;

    @GetMapping("/{activity}")
    public ResponseEntity<ApiResponse<List<GoActivityRatingResponse>>> getRatings(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @PathVariable String activity
    ) {
        ApiResponse<List<GoActivityRatingResponse>> response = service.getRatings(page, size, activity);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GoActivityRatingResponse>> rate(@RequestBody GoActivityRatingRequest request) {
        ApiResponse<GoActivityRatingResponse> response = service.rate(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}