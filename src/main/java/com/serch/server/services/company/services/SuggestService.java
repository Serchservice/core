package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.ServiceSuggestRequest;

public interface SuggestService {
    ApiResponse<String> suggest(ServiceSuggestRequest request);
}
