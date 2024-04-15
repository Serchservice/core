package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.ActivityResponse;
import com.serch.server.services.account.responses.RequestResponse;

public interface ActivityService {
    ApiResponse<ActivityResponse> today();
    ApiResponse<RequestResponse> requests();
}
