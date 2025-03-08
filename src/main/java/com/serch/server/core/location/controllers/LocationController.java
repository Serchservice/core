package com.serch.server.core.location.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.location.responses.Address;
import com.serch.server.core.location.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/location")
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Address>>> predictions(@RequestParam String q) {
        ApiResponse<List<Address>> response = locationService.predictions(q);
        return new ResponseEntity<>(response, response.getStatus());
    }
}