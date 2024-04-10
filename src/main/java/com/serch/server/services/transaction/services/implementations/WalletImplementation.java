package com.serch.server.services.transaction.services.implementations;

import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.transaction.WalletException;
import com.serch.server.models.auth.User;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.WalletUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletImplementation implements WalletService {
    private final WalletRepository walletRepository;

    @Value("${serch.wallet.fund-amount-limit}")
    private Integer FUND_AMOUNT_LIMIT;
    @Value("${serch.wallet.withdraw-amount-limit}")
    private Integer WITHDRAW_AMOUNT_LIMIT;

    @Override
    public void createWallet(User user) {
        if(walletRepository.existsByUser_Id(user.getId())) {
            throw new WalletException("User already owns a wallet");
        }

        if(user.getRole() != Role.ASSOCIATE_PROVIDER) {
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(BigDecimal.valueOf(0.00));
            wallet.setDeposit(BigDecimal.valueOf(0.00));
            walletRepository.save(wallet);
        }
    }
}
