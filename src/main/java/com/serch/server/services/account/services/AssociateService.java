package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.AddAssociateRequest;

import java.util.UUID;

public interface AssociateService {
    ApiResponse<String> add(AddAssociateRequest request);
    ApiResponse<String> delete(UUID id);
    ApiResponse<String> deactivate(UUID id);
    ApiResponse<String> activate(UUID id);
}
