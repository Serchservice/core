package com.serch.server.domains.trip.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.domains.trip.responses.SearchResponse;
import com.serch.server.domains.trip.services.ActiveSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/active/search")
public class ActiveSearchController {
    private final ActiveSearchService service;

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<SearchResponse>> searchProviders(
            @RequestParam(name = "c") SerchCategory category,
            @RequestParam(name = "lng") Double lng,
            @RequestParam(name = "lat") Double lat,
            @RequestParam(required = false, name = "radius") Double radius,
            @RequestParam(required = false, name = "auto") Boolean autoConnect,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<SearchResponse> response = service.search(category, lng, lat, radius, autoConnect, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SearchResponse>> autoConnect(
            @RequestParam(name = "q") String query,
            @RequestParam(name = "lng") Double lng,
            @RequestParam(name = "lat") Double lat,
            @RequestParam(required = false, name = "radius") Double radius,
            @RequestParam(required = false, name = "auto") Boolean autoConnect,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<SearchResponse> response = service.search(query, lng, lat, radius, autoConnect, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }
}