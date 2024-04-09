package com.serch.server.utils;

import com.serch.server.enums.auth.Role;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.services.transaction.requests.BalanceUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletUtil {
    private final WalletRepository walletRepository;
    private final UserUtil userUtil;

    public boolean isBalanceSufficient(BalanceUpdateRequest request) {
        return walletRepository.findByUser_Id(request.getUser())
                .map(wallet -> {
                    if(request.getType() == TransactionType.WITHDRAW) {
                        return wallet.getBalance().compareTo(request.getAmount()) > 0;
                    } else {
                        BigDecimal amount = wallet.getDeposit().add(wallet.getBalance());
                        return amount.compareTo(request.getAmount()) > 0;
                    }
                })
                .orElse(false);
    }

    public void updateBalance(BalanceUpdateRequest request) {
        walletRepository.findByUser_Id(request.getUser())
                .ifPresent(wallet -> {
                    if(request.getType() == TransactionType.WITHDRAW) {
                        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
                    } else if(request.getType() == TransactionType.T2F) {
                        if(wallet.getUser().getRole() == Role.USER) {
                            updateBalance(request, wallet);
                        } else {
                            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                        }
                    } else if (request.getType() == TransactionType.TRIP) {
                        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                    } else {
                        updateBalance(request, wallet);
                    }
                });
    }

    private void updateBalance(BalanceUpdateRequest request, Wallet wallet) {
        if(wallet.getDeposit().compareTo(request.getAmount()) >= 0) {
            wallet.setDeposit(wallet.getDeposit().subtract(request.getAmount()));
        } else {
            request.setAmount(request.getAmount().subtract(wallet.getDeposit()));
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
            wallet.setDeposit(BigDecimal.ZERO);
        }
    }
}
