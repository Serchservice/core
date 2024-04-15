package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;

import java.util.UUID;

public interface ActivityService {
    ApiResponse<ActivityResponse> today();
    ApiResponse<String> goOnline(AddressRequest request);
    ApiResponse<String> requests();
}
