package com.serch.server.core.storage.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.storage.responses.Country;

import java.util.List;

public interface CountryService {
    ApiResponse<List<Country>> countries();
}
