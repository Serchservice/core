package com.serch.server.domains.nearby.services.bcap.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.bcap.services.GoBCapService;
import com.serch.server.domains.nearby.services.bcap.requests.GoBCapCreateRequest;
import com.serch.server.domains.nearby.services.bcap.responses.GoBCapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/go/bcap")
public class GoBCapController {
    private final GoBCapService service;

    @GetMapping
    public ResponseEntity<ApiResponse<GoBCapResponse>> get(@RequestParam String id) {
        ApiResponse<GoBCapResponse> response = service.get(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<GoBCapResponse>>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long interest,
            @RequestParam(required = false, name = "scoped") Boolean scoped,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng
    ) {
        ApiResponse<List<GoBCapResponse>> response = service.getAll(page, size, interest, scoped, lat, lng);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GoBCapResponse>> create(@RequestBody GoBCapCreateRequest request) {
        ApiResponse<GoBCapResponse> response = service.create(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        ApiResponse<Void> response = service.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}