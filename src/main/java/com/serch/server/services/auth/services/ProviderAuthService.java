package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.services.auth.requests.*;
import com.serch.server.services.auth.responses.AuthResponse;

import java.util.List;

public interface ProviderAuthService {
    ApiResponse<AuthResponse> login(RequestLogin request);
    ApiResponse<String> saveProfile(RequestProviderProfile request);
    ApiResponse<String> saveCategory(RequestSerchCategory category);
    ApiResponse<String> saveSpecialties(RequestAuthSpecialty specialty);
    ApiResponse<String> saveAdditional(RequestAdditionalInformation request);
    ApiResponse<AuthResponse> finishSignup(RequestAuth auth);
    void addSpecialtiesToIncompleteProfile(List<Long> specialties, Incomplete incomplete);
    void saveCategory(SerchCategory category, Incomplete incomplete);
    void saveProfile(RequestProviderProfile request, Incomplete incomplete);
    void saveReferral(String code, Incomplete incomplete);
}
