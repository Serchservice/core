package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.company.SpecialtyService;
import com.serch.server.services.auth.responses.SpecialtyResponse;

import java.util.List;

public interface KeywordService {
    ApiResponse<List<SpecialtyResponse>> getAllSpecialties(SerchCategory type);
    SpecialtyResponse getSpecialtyResponse(SpecialtyService keywordService);
}
