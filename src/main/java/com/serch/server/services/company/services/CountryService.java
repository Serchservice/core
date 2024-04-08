package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.CountryRequest;

public interface CountryService {
    ApiResponse<String> checkMyLocation(CountryRequest request);
    ApiResponse<String> requestMyLocation(CountryRequest request);
}
