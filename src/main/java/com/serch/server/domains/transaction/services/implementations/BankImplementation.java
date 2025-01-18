package com.serch.server.domains.transaction.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.core.PaymentService;
import com.serch.server.core.payment.responses.Bank;
import com.serch.server.core.payment.responses.BankAccount;
import com.serch.server.domains.transaction.services.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankImplementation implements BankService {
    private final PaymentService service;

    @Override
    public ApiResponse<List<Bank>> banks() {
        List<Bank> banks = service.banks();
        return new ApiResponse<>(banks);
    }

    @Override
    public ApiResponse<BankAccount> verify(String accountNumber, String code) {
        BankAccount bankAccount = service.verify(accountNumber, code);
        return new ApiResponse<>(bankAccount);
    }
}