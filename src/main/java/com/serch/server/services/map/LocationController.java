package com.serch.server.services.map;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.map.responses.Place;
import com.serch.server.services.map.responses.Prediction;
import com.serch.server.services.map.services.LocationService;
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

    @GetMapping("/search/predict")
    public ResponseEntity<ApiResponse<List<Prediction>>> predictions(@RequestParam String address) {
        var response = locationService.predictions(address);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search/place")
    public ResponseEntity<ApiResponse<Place>> search(@RequestParam String id) {
        var response = locationService.search(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}