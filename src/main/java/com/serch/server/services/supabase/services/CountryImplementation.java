package com.serch.server.services.supabase.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.supabase.core.SupabaseService;
import com.serch.server.services.supabase.responses.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryImplementation implements CountryService {
    private final SupabaseService service;
    @Override
    public ApiResponse<List<Country>> countries() {
        return new ApiResponse<>(service.getCountries());
    }
}
