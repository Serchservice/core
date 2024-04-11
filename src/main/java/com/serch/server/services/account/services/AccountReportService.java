package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.AccountReportRequest;

public interface AccountReportService {
    ApiResponse<String> report(AccountReportRequest request);
}
