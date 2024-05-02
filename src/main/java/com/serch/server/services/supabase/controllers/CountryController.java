package com.serch.server.services.supabase.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.supabase.responses.Country;
import com.serch.server.services.supabase.services.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/country")
public class CountryController {
    private final CountryService service;

    @GetMapping("/countries")
    public ResponseEntity<ApiResponse<List<Country>>> countries() {
        ApiResponse<List<Country>> response = service.countries();
        return new ResponseEntity<>(response, response.getStatus());
    }
}
