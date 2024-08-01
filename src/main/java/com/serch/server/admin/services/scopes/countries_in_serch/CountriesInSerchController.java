package com.serch.server.admin.services.scopes.countries_in_serch;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.countries_in_serch.requests.AddCountryRequest;
import com.serch.server.admin.services.scopes.countries_in_serch.requests.AddStateRequest;
import com.serch.server.admin.services.scopes.countries_in_serch.responses.CountriesInSerchResponse;
import com.serch.server.admin.services.scopes.countries_in_serch.responses.CountryInSerchResponse;
import com.serch.server.bases.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scope/countries-in-serch")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class CountriesInSerchController {
    private final CountriesInSerchService service;

    @GetMapping
    public ResponseEntity<ApiResponse<CountriesInSerchResponse>> overview() {
        ApiResponse<CountriesInSerchResponse> response = service.overview();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/launched")
    public ResponseEntity<ApiResponse<List<CountryInSerchResponse>>> launchedCountries() {
        ApiResponse<List<CountryInSerchResponse>> response = service.launchedCountries();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/requested")
    public ResponseEntity<ApiResponse<List<CountryInSerchResponse>>> requestedCountries() {
        ApiResponse<List<CountryInSerchResponse>> response = service.requestedCountries();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/launched/country/chart")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> launchedCountryChart(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.launchedCountryChart(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/launched/state/chart")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> launchedStateChart(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.launchedStateChart(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/requested/country/chart")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> requestedCountryChart(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.requestedCountryChart(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/requested/state/chart")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> requestedStateChart(@RequestParam Integer year) {
        ApiResponse<List<ChartMetric>> response = service.requestedStateChart(year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/launched/country")
    public ResponseEntity<ApiResponse<List<CountryInSerchResponse>>> add(@Valid @RequestBody AddCountryRequest request) {
        ApiResponse<List<CountryInSerchResponse>> response = service.add(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/launched/state")
    public ResponseEntity<ApiResponse<CountryInSerchResponse>> add(@Valid @RequestBody AddStateRequest request) {
        ApiResponse<CountryInSerchResponse> response = service.add(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/launched/country")
    public ResponseEntity<ApiResponse<CountryInSerchResponse>> toggleStatus(@RequestParam Long id) {
        ApiResponse<CountryInSerchResponse> response = service.toggleStatus(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/launched/country")
    public ResponseEntity<ApiResponse<List<CountryInSerchResponse>>> deleteLaunchedCountry(@RequestParam Long id) {
        ApiResponse<List<CountryInSerchResponse>> response = service.deleteLaunchedCountry(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/launched/state")
    public ResponseEntity<ApiResponse<CountryInSerchResponse>> deleteLaunched(@RequestParam Long id) {
        ApiResponse<CountryInSerchResponse> response = service.deleteLaunched(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/requested/country")
    public ResponseEntity<ApiResponse<List<CountryInSerchResponse>>> deleteRequestedCountry(@RequestParam Long id) {
        ApiResponse<List<CountryInSerchResponse>> response = service.deleteRequestedCountry(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/requested/state")
    public ResponseEntity<ApiResponse<CountryInSerchResponse>> deleteRequested(@RequestParam Long id) {
        ApiResponse<CountryInSerchResponse> response = service.deleteRequested(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
