package com.serch.server.domains.nearby.services.interest.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.bases.BaseLocation;
import com.serch.server.domains.nearby.services.interest.requests.GoInterestRequest;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestCategoryResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestUpdateResponse;
import com.serch.server.domains.nearby.services.interest.services.GoInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/go/interest")
public class GoInterestController {
    private final GoInterestService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoInterestResponse>>> get() {
        ApiResponse<List<GoInterestResponse>> response = service.get();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/update")
    public ResponseEntity<ApiResponse<GoInterestUpdateResponse>> getUpdate() {
        ApiResponse<GoInterestUpdateResponse> response = service.getUpdate();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<GoInterestResponse>>> getAll(
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) String place
    ) {
        BaseLocation location = new BaseLocation(lng, lat, place);

        ApiResponse<List<GoInterestResponse>> response = service.getAll(location);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GoInterestResponse>> get(
            @PathVariable Long id,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) String place
    ) {
        BaseLocation location = new BaseLocation(lng, lat, place);

        ApiResponse<GoInterestResponse> response = service.get(id, location);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<GoInterestCategoryResponse>>> getCategory() {
        ApiResponse<List<GoInterestCategoryResponse>> response = service.getCategory();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/category/all")
    public ResponseEntity<ApiResponse<List<GoInterestCategoryResponse>>> getAllCategories(
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) String place
    ) {
        BaseLocation location = new BaseLocation(lng, lat, place);

        ApiResponse<List<GoInterestCategoryResponse>> response = service.getAllCategories(location);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<ApiResponse<GoInterestCategoryResponse>> getCategory(
            @PathVariable Long id,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) String place
    ) {
        BaseLocation location = new BaseLocation(lng, lat, place);

        ApiResponse<GoInterestCategoryResponse> response = service.getCategory(id, location);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GoInterestUpdateResponse>> add(@RequestBody GoInterestRequest request) {
        ApiResponse<GoInterestUpdateResponse> response = service.add(request.getInterests());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<GoInterestUpdateResponse>> delete(@RequestBody GoInterestRequest request) {
        ApiResponse<GoInterestUpdateResponse> response = service.delete(request.getInterests());
        return new ResponseEntity<>(response, response.getStatus());
    }
}