package com.serch.server.utils;

import com.serch.server.repositories.transaction.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletUtil {
    private final WalletRepository walletRepository;
    private final UserUtil userUtil;

    public boolean isBalanceSufficient(BigDecimal amount, UUID user) {
        return walletRepository.findByUser_Id(user)
                .map(wallet -> wallet.getClearedBalance().compareTo(amount) > 0)
                .orElse(false);
    }


    /**
     * @param amount Amount of money to be added or subtracted from the wallet's balance/withdrawable amount
     * @param isDebit For debit transactions: PROVIDERS and USERS (Using the withdrawable amount)
     */
    public void updateBalance(BigDecimal amount, UUID user, boolean isDebit) {
        walletRepository.findByUser_Id(user)
                .ifPresent(wallet -> {
                    if(isDebit) {
                        wallet.setBalance(wallet.getBalance().subtract(amount));
                    } else {
                        wallet.setBalance(wallet.getBalance().add(amount));
                    }
                });
    }
}
