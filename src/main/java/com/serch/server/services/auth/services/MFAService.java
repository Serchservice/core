package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestMFAChallenge;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.responses.MFADataResponse;
import com.serch.server.services.auth.responses.MFAUsageResponse;

import java.util.List;

public interface MFAService {
    ApiResponse<MFADataResponse> getMFAData();
    ApiResponse<AuthResponse> validateCode(RequestMFAChallenge request);
    ApiResponse<AuthResponse> validateRecoveryCode(RequestMFAChallenge request);
    ApiResponse<List<String>> getRecoveryCodes();
    ApiResponse<String> disable();
    ApiResponse<String> disableRecoveryCode();
    ApiResponse<MFAUsageResponse> usage();
}