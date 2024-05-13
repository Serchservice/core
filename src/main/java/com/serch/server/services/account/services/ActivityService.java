package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.RequestResponse;

public interface ActivityService {
    ApiResponse<RequestResponse> requests();
}
