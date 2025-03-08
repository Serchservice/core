package com.serch.server.domains.rating.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.rating.requests.RateAppRequest;
import com.serch.server.domains.rating.requests.RateRequest;
import com.serch.server.domains.rating.responses.RatingChartResponse;
import com.serch.server.domains.rating.responses.RatingResponse;
import com.serch.server.domains.rating.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rating")
public class RatingController {
    private final RatingService service;

    @GetMapping("/app")
    public ResponseEntity<ApiResponse<RatingResponse>> app(@RequestParam(required = false) String id) {
        ApiResponse<RatingResponse> response = service.app(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> view(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<RatingResponse>> response = service.view(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/bad")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> bad(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<RatingResponse>> response = service.bad(null, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/bad/{id}")
    @PreAuthorize(value = "hasRole('BUSINESS') || hasRole('USER')")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> getBadAssociateRatings(
            @PathVariable String id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<RatingResponse>> response = service.bad(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/good")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> good(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<RatingResponse>> response = service.good(null, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/good/{id}")
    @PreAuthorize(value = "hasRole('BUSINESS') || hasRole('USER')")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> getGoodAssociateRatings(
            @PathVariable String id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<RatingResponse>> response = service.good(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<List<RatingChartResponse>>> chart() {
        ApiResponse<List<RatingChartResponse>> response = service.chart(null);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/{id}")
    @PreAuthorize(value = "hasRole('BUSINESS') || hasRole('USER')")
    public ResponseEntity<ApiResponse<List<RatingChartResponse>>> getAssociateRatingChart(@PathVariable String id) {
        ApiResponse<List<RatingChartResponse>> response = service.chart(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/rate")
    public ResponseEntity<ApiResponse<String>> rate(@RequestBody RateRequest request) {
        ApiResponse<String> response = service.rate(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/rate/app")
    public ResponseEntity<ApiResponse<RatingResponse>> rate(@RequestBody RateAppRequest request) {
        ApiResponse<RatingResponse> response = service.rate(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
