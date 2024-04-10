package com.serch.server.services.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.transaction.responses.TransactionResponse;

import java.util.List;

public interface TransactionService {
    ApiResponse<List<TransactionResponse>> transactions();
}
