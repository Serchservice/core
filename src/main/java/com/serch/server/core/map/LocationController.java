package com.serch.server.core.map;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.map.responses.Address;
import com.serch.server.core.map.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/location")
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Address>>> predictions(@RequestParam String q) {
        ApiResponse<List<Address>> response = locationService.predictions(q);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search/place")
    public ResponseEntity<ApiResponse<Address>> search(@RequestParam String id) {
        ApiResponse<Address> response = locationService.search(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}