package com.serch.server.domains.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.company.requests.ServiceSuggestRequest;

public interface SuggestService {
    ApiResponse<String> suggest(ServiceSuggestRequest request);
}
