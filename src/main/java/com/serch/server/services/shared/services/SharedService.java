package com.serch.server.services.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.responses.SharedAccountResponse;

import java.util.List;

public interface SharedService {
    ApiResponse<AccountResponse> accounts(String id);
    ApiResponse<List<GuestResponse>> links();
}
