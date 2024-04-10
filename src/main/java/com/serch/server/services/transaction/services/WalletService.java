package com.serch.server.services.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.transaction.requests.PayRequest;

public interface WalletService {
    void createWallet(User user);
    ApiResponse<String> pay(PayRequest request);
}
