package com.serch.server.domains.nearby.services.activity.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.activity.requests.GoCreateActivityRequest;
import com.serch.server.domains.nearby.services.activity.responses.GoActivityResponse;
import com.serch.server.domains.nearby.services.activity.services.GoActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/go/activity")
public class GoActivityController {
    private final GoActivityService service;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GoActivityResponse>> get(@PathVariable String id, @RequestParam Double lat, @RequestParam Double lng) {
        ApiResponse<GoActivityResponse> response = service.get(id, lat, lng);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<GoActivityResponse>>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long interest,
            @RequestParam(required = false) LocalDate timestamp,
            @RequestParam(required = false, name = "scoped") Boolean scoped,
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        ApiResponse<List<GoActivityResponse>> response = service.getAll(page, size, interest, timestamp, scoped, lat, lng);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/ongoing")
    public ResponseEntity<ApiResponse<List<GoActivityResponse>>> getAllOngoing(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, name = "scoped") Boolean scoped,
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        ApiResponse<List<GoActivityResponse>> response = service.getAllOngoing(page, size, scoped, lat, lng);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/upcoming")
    public ResponseEntity<ApiResponse<List<GoActivityResponse>>> getAllUpcoming(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, name = "scoped") Boolean scoped,
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        ApiResponse<List<GoActivityResponse>> response = service.getAllUpcoming(page, size, scoped, lat, lng);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/attending")
    public ResponseEntity<ApiResponse<List<GoActivityResponse>>> getAllAttending(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        ApiResponse<List<GoActivityResponse>> response = service.getAllAttending(page, size, lat, lng);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/attended")
    public ResponseEntity<ApiResponse<List<GoActivityResponse>>> getAllAttended(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        ApiResponse<List<GoActivityResponse>> response = service.getAllAttended(page, size, lat, lng);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<GoActivityResponse>>> search(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(name = "q") String query,
            @RequestParam(required = false, name = "scoped") Boolean scoped,
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        ApiResponse<List<GoActivityResponse>> response = service.search(page, size, query, lat, lng, scoped);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<ApiResponse<List<GoActivityResponse>>> getSimilar(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @PathVariable String id,
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        ApiResponse<List<GoActivityResponse>> response = service.getSimilar(page, size, id, lat, lng);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<ApiResponse<List<GoActivityResponse>>> getRelated(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @PathVariable String id,
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        ApiResponse<List<GoActivityResponse>> response = service.getRelated(page, size, id, lat, lng);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/attend/{id}")
    public ResponseEntity<ApiResponse<GoActivityResponse>> attend(@PathVariable String id) {
        ApiResponse<GoActivityResponse> response = service.attend(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/start/{id}")
    public ResponseEntity<ApiResponse<GoActivityResponse>> start(@PathVariable String id) {
        ApiResponse<GoActivityResponse> response = service.start(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/end/{id}")
    public ResponseEntity<ApiResponse<GoActivityResponse>> end(@PathVariable String id) {
        ApiResponse<GoActivityResponse> response = service.end(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GoActivityResponse>> create(@RequestBody GoCreateActivityRequest request) {
        ApiResponse<GoActivityResponse> response = service.create(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        ApiResponse<Void> response = service.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}