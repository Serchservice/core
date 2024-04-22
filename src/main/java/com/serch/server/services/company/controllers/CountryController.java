package com.serch.server.services.company.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.CountryRequest;
import com.serch.server.services.company.services.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company/countries")
public class CountryController {
    private final CountryService countryService;

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyMyLocation(
            @RequestParam String country,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city
    ) {
        CountryRequest request = new CountryRequest();
        request.setCountry(country);
        request.setCity(city);
        request.setState(state);

        ApiResponse<String> response = countryService.checkMyLocation(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<String>> requestMyLocation(@RequestBody CountryRequest request) {
        ApiResponse<String> response = countryService.requestMyLocation(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
