package com.serch.server.services.company.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.CountryRequest;
import com.serch.server.services.company.services.CompanyCountryService;
import com.serch.server.services.supabase.responses.Country;
import com.serch.server.services.supabase.services.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company/countries")
public class CompanyCountryController {
    private final CompanyCountryService companyCountryService;
    private final CountryService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Country>>> countries() {
        ApiResponse<List<Country>> response = service.countries();
        return new ResponseEntity<>(response, response.getStatus());
    }

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

        ApiResponse<String> response = companyCountryService.checkMyLocation(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<String>> requestMyLocation(@RequestBody CountryRequest request) {
        ApiResponse<String> response = companyCountryService.requestMyLocation(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}