package com.serch.server.services.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.transaction.requests.FundRequest;
import com.serch.server.services.transaction.requests.PayRequest;
import com.serch.server.services.transaction.requests.WalletUpdateRequest;
import com.serch.server.services.transaction.requests.WithdrawRequest;
import com.serch.server.services.transaction.responses.WalletResponse;

public interface WalletService {
    void createWallet(User user);
    ApiResponse<String> pay(PayRequest request);
    ApiResponse<String> paySubscription(PayRequest request);
    ApiResponse<String> payTrip(PayRequest request);
    ApiResponse<String> checkIfUserCanPayForTripWithWallet(String trip);
    ApiResponse<InitializePaymentData> fundWallet(FundRequest request);
    ApiResponse<String> verifyFund(String reference);
    ApiResponse<String> requestWithdraw(WithdrawRequest request);
    ApiResponse<WalletResponse> view();
    ApiResponse<String> update(WalletUpdateRequest request);
}
