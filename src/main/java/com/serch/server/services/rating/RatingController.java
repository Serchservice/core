package com.serch.server.services.rating;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.rating.requests.RateAppRequest;
import com.serch.server.services.rating.requests.RateRequest;
import com.serch.server.services.rating.responses.RatingChartResponse;
import com.serch.server.services.rating.responses.RatingResponse;
import com.serch.server.services.rating.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rating")
public class RatingController {
    private final RatingService service;

    @GetMapping("/app")
    public ResponseEntity<ApiResponse<RatingResponse>> app(@RequestParam(required = false) String id) {
        ApiResponse<RatingResponse> response = service.app(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> view() {
        ApiResponse<List<RatingResponse>> response = service.view();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/bad")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> bad() {
        ApiResponse<List<RatingResponse>> response = service.bad(null);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/good")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> good() {
        ApiResponse<List<RatingResponse>> response = service.good(null);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<List<RatingChartResponse>>> chart() {
        ApiResponse<List<RatingChartResponse>> response = service.chart(null);
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
