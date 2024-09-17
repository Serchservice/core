package com.serch.server.services.transaction.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.core.PaymentService;
import com.serch.server.core.payment.responses.Bank;
import com.serch.server.core.payment.responses.BankAccount;
import com.serch.server.services.transaction.services.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NESTED)
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