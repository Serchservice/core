package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.IssueRequest;

public interface IssueService {
    ApiResponse<String> lodgeIssue(IssueRequest request);
}
