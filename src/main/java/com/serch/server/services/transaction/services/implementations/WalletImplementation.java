package com.serch.server.services.transaction.services.implementations;

import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.transaction.WalletException;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.services.transaction.services.WalletService;
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
    public void createWallet(Profile profile) {
        if(walletRepository.existsByProfile_SerchId(profile.getSerchId())) {
            throw new WalletException("User already owns a wallet");
        }

        if(profile.getUser().getRole() != Role.ASSOCIATE_PROVIDER) {
            Wallet wallet = new Wallet();
            wallet.setProfile(profile);
            wallet.setWithdrawableAmount(BigDecimal.valueOf(0.00));
            wallet.setBalance(BigDecimal.valueOf(0.00));
            walletRepository.save(wallet);
        }
    }

    @Override
    public void createWallet(BusinessProfile profile) {
        if(walletRepository.existsByBusinessProfile_SerchId(profile.getSerchId())) {
            throw new WalletException("Business already owns a wallet");
        } else {
            Wallet wallet = new Wallet();
            wallet.setBusinessProfile(profile);
            wallet.setBalance(BigDecimal.valueOf(0.00));
            walletRepository.save(wallet);
        }
    }
}
