package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.RequestCreateProfile;

public interface ProfileService {
    ApiResponse<String> createProfile(RequestCreateProfile request);
}
