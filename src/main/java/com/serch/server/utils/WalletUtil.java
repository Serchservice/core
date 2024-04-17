package com.serch.server.utils;

import com.serch.server.enums.auth.Role;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.services.transaction.requests.BalanceUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * The WalletUtil class provides utility methods for managing user wallets.
 */
@Service
@RequiredArgsConstructor
public class WalletUtil {
    private final WalletRepository repository;

    /**
     * Checks if the balance in the user's wallet is sufficient for a transaction.
     * @param request The balance update request containing user and transaction details.
     * @return True if the balance is sufficient, false otherwise.
     */
    public boolean isBalanceSufficient(BalanceUpdateRequest request) {
        return repository.findByUser_Id(request.getUser())
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

    /**
     * Updates the balance in the user's wallet based on the transaction type.
     * @param request The balance update request containing user and transaction details.
     */
    public void updateBalance(BalanceUpdateRequest request) {
        repository.findByUser_Id(request.getUser())
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
                    } else if (request.getType() == TransactionType.TRIP_WITHDRAW) {
                        updateBalance(request, wallet);
                    } else if (request.getType() == TransactionType.FUNDING) {
                        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                    } else {
                        updateBalance(request, wallet);
                    }
                    repository.save(wallet);
                });
    }

    /**
     * Updates the balance in the user's wallet for transaction types other than withdrawal, transfer, trip, and funding.
     * @param request The balance update request containing user and transaction details.
     * @param wallet The user's wallet entity.
     */
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
