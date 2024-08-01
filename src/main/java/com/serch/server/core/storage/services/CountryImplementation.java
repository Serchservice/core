package com.serch.server.core.storage.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.storage.core.StorageService;
import com.serch.server.core.storage.responses.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryImplementation implements CountryService {
    private final StorageService service;
    @Override
    public ApiResponse<List<Country>> countries() {
        return new ApiResponse<>(service.getCountries());
    }
}
