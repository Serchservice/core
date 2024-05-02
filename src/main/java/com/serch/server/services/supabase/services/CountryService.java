package com.serch.server.services.supabase.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.supabase.responses.Country;

import java.util.List;

public interface CountryService {
    ApiResponse<List<Country>> countries();
}
