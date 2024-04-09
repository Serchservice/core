package com.serch.server.services.conversation.services.implementations;

import com.serch.backend.bases.ApiResponse;
import com.serch.backend.enums.call.CallStatus;
import com.serch.backend.enums.transaction.TransactionStatus;
import com.serch.backend.enums.transaction.TransactionType;
import com.serch.backend.exceptions.SerchException;
import com.serch.backend.platform.call.repositories.CallRepository;
import com.serch.backend.platform.wallet.models.Transaction;
import com.serch.backend.platform.wallet.repositories.TransactionRepository;
import com.serch.backend.platform.wallet.repositories.WalletRepository;
import com.serch.backend.utils.CallUtil;
import com.serch.backend.utils.WalletUtil;
import com.serch.server.services.conversation.services.Tip2FixService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Tip2FixImplementation implements Tip2FixService {
    private final WalletUtil walletUtil;
    private final CallRepository callRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Value("${serch.tip2fix.call-limit}")
    private Integer CALL_LIMIT;

    @Override
    public ApiResponse<String> checkSession(Integer duration, String channel) {
        var call = callRepository.findById(channel)
                .orElseThrow(() -> new SerchException(
                        "An error occurred while locating call. Contact support if this continues."
                ));

        if(CallUtil.getHours(duration) == 1) {
            if(walletUtil.isBalanceSufficient(BigDecimal.valueOf(CALL_LIMIT))) {
                pay(call.getCalled().getSerchId(), call.getCaller().getSerchId());
                call.setSessionCount(call.getSessionCount() + 1);
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);

                return new ApiResponse<>("Continue with Tip2Fix", HttpStatus.OK);
            } else {
                call.setStatus(CallStatus.CLOSED);
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);

                throw new SerchException(
                        "Insufficient balance to start tip2fix. Tip2Fix is charged at ₦%s"
                                .formatted(CALL_LIMIT)
                );
            }
        } else {
            return new ApiResponse<>("Continue with Tip2Fix", HttpStatus.OK);
        }
    }

    @Override
    public void pay(UUID provider, UUID user) {
        var userWallet = walletRepository.findByProfile_SerchId(user)
                .orElseThrow(() -> new SerchException("User not found"));
        var providerWallet = walletRepository.findByProfile_SerchId(provider)
                .orElseThrow(() -> new SerchException("User not found"));

        userWallet.setBalance(userWallet.getBalance().subtract(BigDecimal.valueOf(CALL_LIMIT)));
        walletRepository.save(userWallet);

        providerWallet.setBalance(providerWallet.getBalance().add(BigDecimal.valueOf(CALL_LIMIT)));
        walletRepository.save(providerWallet);

        Transaction transaction = new Transaction();
        transaction.setReceiver(providerWallet);
        transaction.setSender(userWallet);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setType(TransactionType.TIP2FIX);
        transaction.setAmount(BigDecimal.valueOf(CALL_LIMIT));
        transactionRepository.save(transaction);
    }
}
