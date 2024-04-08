package com.serch.server.services.countries.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.countries.CountryRequest;

public interface CountryService {
    ApiResponse<String> checkMyLocation(CountryRequest request);
    ApiResponse<String> requestMyLocation(CountryRequest request);
}
